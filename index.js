const express = require('express');
const bodyParser = require('body-parser');
const axios = require('axios');
const { OpenAI } = require('openai');
require('dotenv').config();

console.log("🔑 Using API-Football key:", process.env.API_FOOTBALL_KEY ? "Loaded ✅" : "Missing ❌");

const app = express();
const port = 3000;

app.use(bodyParser.json());

const openai = new OpenAI({ apiKey: process.env.OPENAI_API_KEY });

// Headers for API-Football
const apiHeaders = {
  'x-apisports-key': process.env.API_FOOTBALL_KEY
};

const teamIds = {
  "Arsenal": 42,
  "Chelsea": 49,
  "Liverpool": 40,
  "Manchester City": 50,
  "Man City": 50,
  "Manchester United": 33,
  "Man United": 33,
  "Tottenham": 47,
  "Spurs": 47,
  "Newcastle": 34,
  "Brighton": 51,
  "Brentford": 55,
  "Crystal Palace": 52,
  "Everton": 45,
  "Fulham": 36,
  "West Ham": 48,
  "Wolves": 39,
  "Nottingham Forest": 65,
  "Aston Villa": 66,
  "Bournemouth": 35,
  "Sheffield United": 62,
  "Luton": 71,
  "Leicester": 46,
  "Leeds": 63,
  "Southampton": 41
};

async function getTeamForm(teamName) {
  const teamId = teamIds[teamName];
  if (!teamId) return "Form not available";

  try {
    const response = await axios.get(
      `https://v3.football.api-sports.io/fixtures?team=${teamId}&season=2023&status=FT`,
      { headers: apiHeaders }
    );

    console.log(`🟡 DEBUG raw form data for ${teamName}:`, response.data);

    const last5 = response.data.response.slice(0, 5);
    const form = last5.map(match => {
      const isHome = match.teams.home.id === teamId;
      const result = match.teams.home.winner === true ? "HOME" :
                     match.teams.away.winner === true ? "AWAY" : "DRAW";
      return (result === "DRAW") ? "D" :
             ((isHome && result === "HOME") || (!isHome && result === "AWAY")) ? "W" : "L";
    }).join("-");

    console.log(`DEBUG - ${teamName} form:`, form);
    return form;

  } catch (error) {
    console.error(`❌ Failed to fetch form for ${teamName}:`, error.response?.data || error.message);
    return "Form error";
  }
}

async function getKeyPlayers(teamName) {
  const teamId = teamIds[teamName];
  if (!teamId) return `${teamName}: Unknown`;

  let keyPlayers = [];

  try {
    let page = 1;
    let hasMore = true;

    while (hasMore && keyPlayers.length < 3) {
      const response = await axios.get(
        `https://v3.football.api-sports.io/players?team=${teamId}&season=2023&page=${page}`,
        { headers: apiHeaders }
      );

      console.log(`🟡 DEBUG raw player data for ${teamName}, page ${page}:`, response.data);

      const players = response.data.response;

      const filtered = players.filter(p => {
        const stats = p.statistics[0];
        return stats && ["Attacker", "Midfielder"].includes(stats.games.position);
      });

      keyPlayers.push(...filtered.map(p => p.player.name));

      const totalPages = response.data.paging.total;
      page++;
      hasMore = page <= totalPages;
    }

    const top = keyPlayers.slice(0, 3);
    const result = `${teamName}: ${top.join(", ")}`;
    console.log(`DEBUG - Key Players for ${teamName}:`, result);
    return result;

  } catch (error) {
    console.error(`❌ Failed to fetch key players for ${teamName}:`, error.response?.data || error.message);
    return `${teamName}: Key players not available`;
  }
}

app.post('/match-preview', async (req, res) => {
  const { team1, team2 } = req.body;
  if (!team1 || !team2) return res.status(400).json({ error: "Missing team names" });

  const [form1, form2, keyPlayers1, keyPlayers2] = await Promise.all([
    getTeamForm(team1),
    getTeamForm(team2),
    getKeyPlayers(team1),
    getKeyPlayers(team2)
  ]);

  console.log("DEBUG - Form 1:", form1);
  console.log("DEBUG - Form 2:", form2);
  console.log("DEBUG - Key Players:", `${keyPlayers1}; ${keyPlayers2}`);

  const prompt = `
You are a football analyst tasked with generating a detailed match preview for today's Premier League game between ${team1} and ${team2}.

Use the following information:
- ${team1} recent form: ${form1}
- ${team2} recent form: ${form2}
- Key players to watch: ${keyPlayers1}; ${keyPlayers2}

Follow these steps in your analysis:
1. **Team Form:** Evaluate recent performance trends for both teams (last 5 matches).
2. **Key Player Impact:** Analyze how the listed key players may influence the game.
3. **Historical Performance:** Briefly mention recent head-to-head results if relevant.
4. **Home/Away Factor:** Consider if either team has a significant advantage or disadvantage.
5. **Prediction Reasoning:** Combine the above to explain your reasoning.
6. **Predicted Outcome:** Conclude with a likely result and potential scoreline.

# Output Format:
- **Match:** ${team1} vs ${team2}
- **Prediction Reasoning:** [Brief but insightful analysis]
- **Predicted Outcome:** [Likely result and scoreline]

Note: Your prediction should be professional and informative for football fans. Be logical, avoid bias, and clearly explain uncertainty where applicable.`;

  try {
    const completion = await openai.chat.completions.create({
      model: "gpt-4",
      messages: [{ role: "user", content: prompt }],
      temperature: 0.7,
      max_tokens: 500
    });

    const preview = completion.choices[0].message.content;
    res.json({ preview });
  } catch (error) {
    console.error("❌ Error generating match preview:", error.response?.data || error.message);
    res.status(500).json({ error: "Failed to generate match preview" });
  }
});

app.listen(port, '0.0.0.0', () => {
  console.log(`🚀 Server running on http://localhost:${port}`);
});
