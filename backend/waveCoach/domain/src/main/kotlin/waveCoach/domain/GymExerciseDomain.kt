package waveCoach.domain

import org.springframework.stereotype.Component

@Component
class GymExerciseDomain {
    fun isNameValid(name: String) = name.length in 1..64

    fun isCategoryValid(category: String) = Category.toCategory(category) != null
}
