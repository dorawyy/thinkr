package com.example.thinkr.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.repositories.quiz.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    private var _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    suspend fun loadQuiz(documentItem: Document) {
        val quiz = quizRepository.getQuiz(documentItem)
        _state.update { it.copy(quiz = quiz, selectedAnswers = List(quiz.size) { "" }, totalTimeSeconds = quiz.size * 15) }
    }

    fun onBackPressed(navController: NavController) {
        navController.popBackStack()
    }

    fun onStartQuiz() {
        _state.update {
            it.copy(started = true)
        }
    }

    fun onQuizTimeUp() {
        _state.update {
            it.copy(
                revealAnswer = true,
                totalScore = it.selectedAnswers.mapIndexed { index, selectedIndex ->
                    if (selectedIndex == it.quiz[index].answer) 1 else 0
                }.reduce { sum, element -> sum + element }
            )
        }
    }

    fun onAnswerSelected(questionIndex: Int, answerKey: String) {
        _state.update {
            it.copy(
                selectedAnswers = it.selectedAnswers.toMutableList().apply {
                    this[questionIndex] = answerKey
                }
            )
        }
    }
}
