package waveCoach.http.model.input

data class AthleteUpdateCharacteristicsInputModel (
    val weight: Float?,
    val height: Int?,
    val calories: Int?,
    val bodyFat: Float?,
    val waistSize: Int?,
    val armSize: Int?,
    val thighSize: Int?,
    val tricepFat: Int?,
    val abdomenFat: Int?,
    val thighFat: Int?
)