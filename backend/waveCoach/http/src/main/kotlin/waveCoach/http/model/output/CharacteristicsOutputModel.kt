package waveCoach.http.model.output

data class CharacteristicsOutputModel(
    val date: Long,
    val uid: Int,
    val weight: Float?,
    val height: Int?,
    val bmi: Float?,
    val calories: Int?,
    val bodyFat: Float?,
    val waistSize: Int?,
    val armSize: Int?,
    val thighSize: Int?,
    val tricepFat: Int?,
    val abdomenFat: Int?,
    val thighFat: Int?
)