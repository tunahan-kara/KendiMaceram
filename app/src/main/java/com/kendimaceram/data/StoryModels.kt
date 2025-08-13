// StoryModels.kt DOSYASININ TAM İÇERİĞİ

package com.kendimaceram.app.data

// Kullanıcının yapabileceği bir seçimi temsil eder.
data class Choice(
    val text: String,
    val nextNodeId: String
)

// Hikayenin bir parçasını (düğümünü) temsil eder.
data class StoryNode(
    val id: String,
    val text: String,
    val choices: List<Choice>
)

// UI'ın ihtiyacı olan tüm veriyi bir arada tutan yardımcı data class.
// ViewModel'den BAĞIMSIZ, kendi dosyasında duruyor.
data class StoryUiState(
    val currentNode: StoryNode? = null
)