package com.example.thinkr.ui.home

import android.net.Uri
import com.example.thinkr.data.models.Document

sealed class HomeScreenAction {
    data object BackButtonClicked : HomeScreenAction()
    data object ProfileButtonClicked : HomeScreenAction()
    data class DocumentItemClicked(val documentItem: Document) : HomeScreenAction()
    data object AddButtonClicked : HomeScreenAction()
    data object DismissDialog : HomeScreenAction()
    data class FileSelected(val selectedUri: Uri) : HomeScreenAction()
}
