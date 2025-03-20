package waveCoach.http.model.output

data class CharacteristicsOutputModel(
    val date: Long,
    val uid: Int,
    val height: Int?,
    val weight: Float?,
    val calories: Int?,
    val waist: Int?,
    val arm: Int?,
    val thigh: Int?,
    val tricep: Float?,
    val abdominal: Float?
)