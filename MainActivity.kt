package com.example.match_preview_api

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class MainActivity : AppCompatActivity() {

    private lateinit var homeTeamEditText: EditText
    private lateinit var awayTeamEditText: EditText
    private lateinit var matchPreviewButton: Button
    private lateinit var previewTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeTeamEditText = findViewById(R.id.homeTeamEditText)
        awayTeamEditText = findViewById(R.id.awayTeamEditText)
        matchPreviewButton = findViewById(R.id.matchPreviewButton)
        previewTextView = findViewById(R.id.previewTextView)

        matchPreviewButton.setOnClickListener {
            val team1 = homeTeamEditText.text.toString().trim()
            val team2 = awayTeamEditText.text.toString().trim()

            if (team1.isEmpty() || team2.isEmpty()) {
                Toast.makeText(this, "Please enter both teams", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = MatchPreviewRequest(
                team1 = team1,
                team2 = team2
            )

            previewTextView.text = "Generating preview..."

            RetrofitClient.service.getMatchPreview(request)
                .enqueue(object : Callback<MatchPreviewResponse> {
                    override fun onResponse(
                        call: Call<MatchPreviewResponse>,
                        response: Response<MatchPreviewResponse>
                    ) {
                        val preview = response.body()?.preview
                        previewTextView.text = preview ?: "No preview available"
                    }

                    override fun onFailure(call: Call<MatchPreviewResponse>, t: Throwable) {
                        val errorText = "API Call Failed: ${t.message}"
                        Log.e("API_ERROR", errorText)
                        previewTextView.text = errorText
                    }
                })
        }
    }
}
