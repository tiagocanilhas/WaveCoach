package waveCoach.host

object Environment {
    // Database URL
    fun getDbUrl() = System.getenv(KEY_DB_URL) ?: throw Exception("Missing env var $KEY_DB_URL")

    private const val KEY_DB_URL = "DB_URL"

    // Cloudinary credentials
    fun getCloudName() = System.getenv(KEY_CLOUD_NAME) ?: throw Exception("Missing env var $KEY_CLOUD_NAME")

    fun getApiKey() = System.getenv(KEY_API_KEY) ?: throw Exception("Missing env var $KEY_API_KEY")

    fun getApiSecret() = System.getenv(KEY_API_SECRET) ?: throw Exception("Missing env var $KEY_API_SECRET")

    private const val KEY_CLOUD_NAME = "CLOUDINARY_CLOUD_NAME"
    private const val KEY_API_KEY = "CLOUDINARY_API_KEY"
    private const val KEY_API_SECRET = "CLOUDINARY_API_SECRET"
}
