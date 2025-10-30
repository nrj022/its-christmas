package com.example.itshalloween.ui.screens.cardeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.itshalloween.R
import com.example.itshalloween.ui.theme.Orange
import com.example.itshalloween.ui.theme.SoftBlack
import com.example.itshalloween.ui.theme.White
import kotlin.math.roundToInt

/**
 * 드래그 가능한 텍스트의 상태를 저장하는 데이터 클래스
 * @param id 고유 식별자
 * @param text 표시될 텍스트
 * @param offset 화면 상의 위치 (Offset)
 */
data class DraggableTextInfo(
    val id: Int,
    var text: String,
    var offset: Offset
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEditorScreen(
    onBackClicked: () -> Unit = {},
    onShareClicked: () -> Unit = {}
) {
    // 동적으로 추가되고 드래그 가능한 텍스트 목록을 관리하는 상태
    val draggableTexts = remember { mutableStateListOf<DraggableTextInfo>() }
    var nextId by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = Orange,
        topBar = {
            // 상단 바: 뒤로가기 버튼과 Share 버튼
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClicked,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(White.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SoftBlack
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onShareClicked,
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = SoftBlack),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "Share",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        // Box를 사용하여 이미지와 드래그 가능한 텍스트들을 겹쳐서 배치
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                // 화면의 빈 공간을 클릭하면 새 텍스트를 추가하는 로직
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        draggableTexts.add(
                            DraggableTextInfo(
                                id = nextId++,
                                text = "New Text",
                                offset = offset
                            )
                        )
                    }
                }
        ) {
            // 중앙 이미지
            Image(
                painter = painterResource(id = R.drawable.img_sample),
                contentDescription = "Card Image",
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .offset(y = (-80).dp), // 이미지를 약간 위로 올림
                contentScale = ContentScale.Fit
            )

            // 동적으로 추가된 텍스트들을 화면에 표시
            draggableTexts.forEachIndexed { index, textInfo ->
                DraggableEditableTextField(
                    textInfo = textInfo,
                    onTextChange = { newText ->
                        // 텍스트 내용이 변경되면 상태 업데이트
                        draggableTexts[index] = textInfo.copy(text = newText)
                    },
                    onDrag = { dragAmount ->
                        // 드래그하여 위치가 변경되면 상태 업데이트
                        draggableTexts[index] = textInfo.copy(offset = textInfo.offset + dragAmount)
                    }
                )
            }
        }
    }
}

/**
 * 드래그 및 편집이 가능한 커스텀 텍스트 필드
 */
@Composable
fun DraggableEditableTextField(
    textInfo: DraggableTextInfo,
    onTextChange: (String) -> Unit,
    onDrag: (Offset) -> Unit
) {
    // BasicTextField를 Box로 감싸서 위치를 지정하고 드래그 제스처를 감지
    Box(
        modifier = Modifier
            .offset {
                // 상태에 저장된 offset 값으로 위치를 지정
                IntOffset(textInfo.offset.x.roundToInt(), textInfo.offset.y.roundToInt())
            }
            .pointerInput(textInfo.id) { // id를 key로 사용하여 각 텍스트 필드가 독립적으로 동작하도록 함
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount) // 드래그 양만큼 위치를 업데이트하도록 콜백 호출
                }
            }
    ) {
        BasicTextField(
            value = textInfo.text,
            onValueChange = onTextChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = SoftBlack,
                textAlign = TextAlign.Center
            ),
            cursorBrush = SolidColor(SoftBlack)
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun CardEditorScreenPreview() {
    CardEditorScreen()
}
