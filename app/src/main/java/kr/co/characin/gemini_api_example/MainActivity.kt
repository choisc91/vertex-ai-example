package kr.co.characin.gemini_api_example

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kr.co.characin.gemini_api_example.MainUi.Companion.BinaryChoiceRadioButton
import kr.co.characin.gemini_api_example.ui.theme.GeminiapiexampleTheme
import kotlin.getValue

class MainActivity : ComponentActivity() {

    private val _viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeminiapiexampleTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    // 전체 영역.
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .padding(all = 8.dp),
                    ) {
                        // 생성되는 프롬프트를 보여주기위한 영역.
                        val messages = _viewModel.message
                        MainUi.BuildMessagesContainer(
                            modifier = Modifier.weight(1f),
                            messages = messages,
                        )

                        // 프롬프트를 실행할 엔진을 선택하는 라디오 버튼.
                        var selectedOption by remember { mutableStateOf("gemini-2.0") }
                        BinaryChoiceRadioButton(
                            selectedOption = selectedOption,
                            onValueChange = { selectedOption = it },
                        )

                        // 실행할 프롬프트를 입력하는 텍스트 필드.
                        var prompt by remember { mutableStateOf(value = "") }
                        var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

                        MainUi.BuildInputPromptField(
                            prompt = prompt,
                            bitmap = imageBitmap,
                            onPromptChange = { prompt = it },
                            onBitmapChange = { imageBitmap = it },
                        )

                        // 입력된 프롬프트를 실행하는 버튼.
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(size = 8.dp),
                            onClick = {
                                if (selectedOption == "imagen3")
                                    _viewModel.sendRequestImagePrompt(prompt)
                                else
                                    _viewModel.sendPrompt(prompt, imageBitmap)

                                prompt = ""
                                imageBitmap = null
                            },
                            content = { Text("SEND") }
                        )
                    }

                    // 프롬프트 실행 후 비동기 처리를 보여주기 위한 프로그레스 바.
                    val inProgress by _viewModel.inProgressFlow.collectAsState()
                    if (inProgress)
                        MainUi.BuildProgressDialog()
                }
            }
        }
    }
}