package com.example.thinkr.ui.home

import android.net.Uri
import com.example.thinkr.data.models.Document

/**
 * Sealed class representing different user actions that can be performed on the home screen.
 *
 * Used to communicate user intents from the UI to the ViewModel for processing.
 */
sealed class HomeScreenAction {
    /**
     * Action triggered when the back navigation button is clicked.
     */
    data object BackButtonClicked : HomeScreenAction()

    /**
     * Action triggered when the profile button is clicked.
     */
    data object ProfileButtonClicked : HomeScreenAction()

    /**
     * Action triggered when a document item is clicked.
     *
     * @property documentItem The document that was clicked.
     */
    data class DocumentItemClicked(val documentItem: Document) : HomeScreenAction()

    /**
     * Action triggered when the add document button is clicked.
     */
    data object AddButtonClicked : HomeScreenAction()

    /**
     * Action triggered when the document dialog is dismissed.
     */
    data object DismissDialog : HomeScreenAction()

    /**
     * Action triggered when a file is selected from the file picker.
     *
     * @property selectedUri URI of the selected file.
     */
    data class FileSelected(val selectedUri: Uri) : HomeScreenAction()
}
