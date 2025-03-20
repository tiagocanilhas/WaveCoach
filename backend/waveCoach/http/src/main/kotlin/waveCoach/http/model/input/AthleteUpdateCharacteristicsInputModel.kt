package waveCoach.http.model.input

data class AthleteUpdateCharacteristicsInputModel (
    val weight: Float?,
    val height: Int?,
    val calories: Int?,
    val waist: Int?,
    val arm: Int?,
    val thigh: Int?,
    val tricep: Float?,
    val abdominal: Float?
)