package kr.co.characin.gemini_api_example

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

class MainUi {
    companion object {
        @Composable
        fun BuildProgressDialog() {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(size = 32.dp),
                        color = Color(color = 0xffB93920),
                        trackColor = Color(color = 0xffECECEC),
                    )
                }
            }
        }

        @Composable
        fun BuildInputPromptField(
            bitmap: Bitmap? = null,
            prompt: String,
            onPromptChange: (String) -> Unit,
            onBitmapChange: (Bitmap?) -> Unit,
        ) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicturePreview(),
                onResult = { onBitmapChange(it) }
            )

            bitmap?.let {
                Box(
                    modifier = Modifier
                        .background(color = Color.White)
                        .padding(4.dp),
                    content = {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "촬영된 사진",
                            modifier = Modifier.height(80.dp)
                        )
                    },
                )
            }

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = prompt,
                shape = RoundedCornerShape(4.dp),
                onValueChange = onPromptChange,
                leadingIcon = {
                    IconButton(
                        onClick = { launcher.launch(null) },
                        content = {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                contentDescription = "camera",
                                imageVector = ImageVector.vectorResource(R.drawable.ic_camera),
                            )
                        },
                    )
                },

                trailingIcon = {
                    IconButton(
                        onClick = {
                            onPromptChange("")
                            onBitmapChange(null)
                        },
                        content = {
                            Icon(
                                modifier = Modifier.size(16.dp),
                                contentDescription = "clear",
                                imageVector = ImageVector.vectorResource(R.drawable.ic_clear),
                            )
                        },
                    )
                }
            )
        }

        @Composable
        fun BuildMessagesContainer(
            modifier: Modifier,
            messages: List<Message>,
        ) {
            val listState = rememberLazyListState()
            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty())
                    listState.animateScrollToItem(messages.lastIndex)
            }

            LazyColumn(
                modifier = modifier,
                state = listState
            ) {
                items(count = messages.size) { index ->
                    BuildMessageItem(message = messages[index])
                    Spacer(Modifier.height(16.dp))
                }
            }
        }

        @Composable
        fun BuildMessageItem(message: Message) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (message.type == Message.TYPE_PROMPT) Arrangement.End else Arrangement.Start,
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            color = if (message.type == Message.TYPE_PROMPT)
                                Color.Blue
                            else
                                Color.Yellow,
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(8.dp),
                    horizontalAlignment = if (message.type == Message.TYPE_PROMPT) Alignment.End else Alignment.Start,
                ) {
                    Text(
                        message.text,
                        color = if (message.type == Message.TYPE_PROMPT)
                            Color.White
                        else
                            Color.Black,
                    )

                    if (message.bitmap != null) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Image(
                            bitmap = message.bitmap.asImageBitmap(),
                            contentDescription = "created image",
                        )
                    }
                }
            }
        }

        @Composable
        fun BinaryChoiceRadioButton(
            selectedOption: String,
            onValueChange: (String) -> Unit,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedOption == "gemini-2.0",
                    onClick = { onValueChange("gemini-2.0") }
                )

                Text(
                    text = "gemini-2.0",
                    modifier = Modifier.clickable { onValueChange("gemini-2.0") }
                )

                RadioButton(
                    selected = selectedOption == "imagen3",
                    onClick = { onValueChange("imagen3") }
                )

                Text(
                    text = "imagen3",
                    modifier = Modifier
                        .clickable { onValueChange("imagen3") }
                        .padding(end = 16.dp)
                )
            }
        }
    }
}