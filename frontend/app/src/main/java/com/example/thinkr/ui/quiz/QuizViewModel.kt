package com.example.thinkr.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.thinkr.data.models.Document
import com.example.thinkr.data.models.QuizSuggestion
import com.example.thinkr.data.repositories.quiz.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing quiz data and user interactions during a quiz session.
 *
 * @property quizRepository Repository used to fetch quiz questions and data.
 */
class QuizViewModel(private val quizRepository: QuizRepository) : ViewModel() {
    private var _state = MutableStateFlow(QuizState())
    val state = _state.asStateFlow()

    /**
     * Loads a quiz based on a document.
     *
     * @param documentItem The document from which to generate the quiz.
     */
    suspend fun loadQuiz(documentItem: Document) {
        val quiz = quizRepository.getQuiz(documentItem)
        _state.update {
            it.copy(
                quiz = quiz,
                selectedAnswers = List(quiz.size) { "" },
                totalTimeSeconds = quiz.size * 15
            )
        }
    }

    /**
     * Loads a pre-suggested quiz.
     *
     * @param suggestedQuiz The suggested quiz to load.
     */
    fun loadSuggestedQuiz(suggestedQuiz: QuizSuggestion) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    quiz = suggestedQuiz.quiz.toMutableList(),
                    selectedAnswers = List(suggestedQuiz.quiz.size) { "" },
                    totalTimeSeconds = 15 * suggestedQuiz.quiz.size
                )
            }
        }
    }

    /**
     * Handles back button press by navigating back in the navigation stack.
     *
     * @param navController The navigation controller to handle navigation.
     */
    fun onBackPressed(navController: NavController) {
        navController.popBackStack()
    }

    /**
     * Sets the quiz state to started.
     */
    fun onStartQuiz() {
        _state.update {
            it.copy(started = true)
        }
    }

    /**
     * Handles the end of the quiz timer by revealing answers and calculating the total score.
     */
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

    /**
     * Updates the selected answer for a specific question.
     *
     * @param questionIndex The index of the question being answered.
     * @param answerKey The key/identifier of the selected answer.
     */
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
