package com.devfusion.movielens

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ChatbaseScreen() {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = WebSettings.LOAD_DEFAULT

                // Important: Enable these for proper rendering
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        // Chatbase widget should be visible now
                    }
                }

                // Load HTML with proper viewport and styling
                loadDataWithBaseURL(
                    "https://www.chatbase.co",
                    getChatbaseHtml(),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
        }
    )
}

private fun getChatbaseHtml(): String {
    return """
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
        <title>MovieLens Assistant</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            body {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                font-family: Arial, sans-serif;
                color: black;
                padding: 20px;
                position: relative;
                overflow: hidden;
            }
            .welcome {
                text-align: center;
                padding: 20px;
                max-width: 90%;
                z-index: 1;
            }
            .welcome h2 {
                font-size: 24px;
                margin-bottom: 10px;
            }
            .welcome p {
                font-size: 16px;
                margin-bottom: 8px;
                line-height: 1.4;
            }
            .chat-container {
                position: fixed;
                bottom: 20px;
                right: 20px;
                width: 60px;
                height: 60px;
                z-index: 1000;
            }
            /* Force chat button to be visible */
            .chatbase-button {
                position: fixed !important;
                bottom: 20px !important;
                right: 20px !important;
                width: 60px !important;
                height: 60px !important;
                background: #4CAF50 !important;
                border-radius: 50% !important;
                box-shadow: 0 4px 12px rgba(0,0,0,0.3) !important;
                z-index: 1000 !important;
            }
        </style>
        
        <!-- Chatbase Configuration -->
        <script>
            window.chatbaseConfig = {
                chatbotId: "-M6gYYWYQYQx8wOveuTEl",
            };
        </script>
        <script
            src="https://www.chatbase.co/embed.min.js"
            id="-M6gYYWYQYQx8wOveuTEl"
            defer>
        </script>
    </head>
    <body>
        <div class="welcome">
            <h2>🎬 MovieLens Assistant</h2>
            <p>Your personal movie recommendation expert</p>
            <p>Ask me for:</p>
            <p>• Movies similar to ones you love</p>
            <p>• Recommendations by genre or mood</p>
            <p>• Hidden gems you might have missed</p>
            <br>
            <p><strong>Tap the green chat button in the bottom-right corner! 👇</strong></p>
            <h3>COMING SOON</h3>
        </div>
        
        <!-- Container for chat button -->
        <div class="chat-container" id="chat-button-container"></div>
        
        <script>
            // Wait for Chatbase to load and ensure button is visible
            setTimeout(function() {
                // Try to find and style the chat button
                const chatButton = document.querySelector('[data-chatbase]');
                if (chatButton) {
                    chatButton.classList.add('chatbase-button');
                    chatButton.style.position = 'fixed';
                    chatButton.style.bottom = '20px';
                    chatButton.style.right = '20px';
                    chatButton.style.width = '60px';
                    chatButton.style.height = '60px';
                    chatButton.style.backgroundColor = '#4CAF50';
                    chatButton.style.borderRadius = '50%';
                    chatButton.style.boxShadow = '0 4px 12px rgba(0,0,0,0.3)';
                    chatButton.style.zIndex = '1000';
                }
                
                // Also try to open chat programmatically
                if (window.chatbase && typeof window.chatbase === 'function') {
                    window.chatbase('openChat');
                }
            }, 3000);
        </script>
    </body>
    </html>
    """.trimIndent()
}