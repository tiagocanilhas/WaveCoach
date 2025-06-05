package waveCoach.services

import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class CloudinaryServices(
    private val cloudinary: Cloudinary,
) {
    private fun uploadImage(
        file: MultipartFile,
        folder: String = "",
    ): String? {
        val options =
            mapOf(
                "folder" to folder,
                "transformation" to "c_fit,h_500,w_500"
            )

        val res = cloudinary.uploader().upload(file.bytes, options)
        return res["secure_url"] as String?
    }

    fun uploadAthleteImage(file: MultipartFile): String? = uploadImage(file, "athletes")

    fun uploadExerciseImage(file: MultipartFile): String? = uploadImage(file, "exercises")

    fun uploadManeuverImage(file: MultipartFile): String? = uploadImage(file, "maneuvers")

    fun deleteImage(url: String): Boolean {
        val publicId = url.substringAfterLast("/").substringBeforeLast(".")
        return try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
            true
        } catch (e: Exception) {
            false
        }
    }
}
