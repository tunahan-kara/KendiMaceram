package com.kendimaceram.app.data

object StoryRepository {

    private val storyData = mapOf(
        "start" to StoryNode(
            id = "start",
            text = "Yemyeşil bir ormanın derinliklerinde, ikiye ayrılan bir patikada duruyorsun. Soldaki patika karanlık bir mağaraya, sağdaki ise ışıl ışıl parlayan bir nehre gidiyor.",
            choices = listOf(
                Choice(text = "Mağaraya gir.", nextNodeId = "cave"),
                Choice(text = "Nehre doğru yürü.", nextNodeId = "river")
            )
        ),
        "cave" to StoryNode(
            id = "cave",
            text = "Mağara nemli ve soğuk. İçeriden gelen hırıltıları duyuyorsun. Bu bir ejderha olabilir! Savaşmak yerine sessizce geri dönmelisin belki de.",
            choices = listOf(
                Choice(text = "Geri dön!", nextNodeId = "cave_end_flee"),
                Choice(text = "Savaş!", nextNodeId = "cave_end_fight")
            )
        ),
        "river" to StoryNode(
            id = "river",
            text = "Nehrin kenarında sihirli bir balık oltasına takılmış. Onu kurtarırsan bir dileğini gerçekleştireceğini söylüyor. Balığa inanmalı mısın?",
            choices = listOf(
                Choice(text = "Balığı kurtar.", nextNodeId = "river_end_save"),
                Choice(text = "Boş ver, yoluna devam et.", nextNodeId = "river_end_ignore")
            )
        ),
        // Hikayenin Sonları (choice listeleri boş)
        "cave_end_flee" to StoryNode("cave_end_flee", "Akıllıca bir seçim! Bazen en büyük kahramanlık, ne zaman geri çekileceğini bilmektir. Maceran burada sona erdi.", choices = emptyList()),
        "cave_end_fight" to StoryNode("cave_end_fight", "Cesurca savaştın ama ejderha çok güçlüydü. Bu macera senin için kötü bitti.", choices = emptyList()),
        "river_end_save" to StoryNode("river_end_save", "Balığı kurtardın ve o da dileğini gerçekleştirdi! Artık krallığın en zengin kahramanısın. Tebrikler!", choices = emptyList()),
        "river_end_ignore" to StoryNode("river_end_ignore", "Balığı umursamadın ve yoluna devam ettin, ama sihirli bir fırsatı kaçırdığından habersizdin. Maceran sıradan bir şekilde sona erdi.", choices = emptyList())
    )

    fun getNode(id: String): StoryNode? {
        return storyData[id]
    }
}