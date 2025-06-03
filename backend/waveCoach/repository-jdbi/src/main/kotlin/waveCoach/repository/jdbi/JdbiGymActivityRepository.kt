package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.Exercise
import waveCoach.domain.GymActivity
import waveCoach.domain.GymExercise
import waveCoach.domain.Sets
import waveCoach.repository.GymActivityRepository

class JdbiGymActivityRepository(
    private val handle: Handle,
) : GymActivityRepository {
    override fun storeGymActivity(activityId: Int): Int =
        handle.createUpdate("insert into waveCoach.gym (activity) values (:activity)")
            .bind("activity", activityId)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getGymActivityList(uid: Int): List<GymActivity> =
        handle.createQuery(
            """
            select * from waveCoach.gym where activity in (select id from waveCoach.activity where uid = :uid)
            """.trimIndent(),
        )
            .bind("uid", uid)
            .mapTo<GymActivity>()
            .list()

    override fun removeGymActivities(uid: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.gym where activity in (select id from waveCoach.activity where uid = :uid)
            """.trimIndent(),
        )
            .bind("uid", uid)
            .execute()
    }

    override fun removeGymActivity(activityId: Int) {
        handle.createUpdate("delete from waveCoach.gym where activity = :activityId")
            .bind("activityId", activityId)
            .execute()
    }

    override fun storeExercise(
        activityID: Int,
        exerciseID: Int,
        exerciseOrder: Int,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.exercise (activity, exercise, exercise_order) 
            values (:activity, :exercise, :exerciseOrder)
            """.trimIndent(),
        )
            .bind("activity", activityID)
            .bind("exercise", exerciseID)
            .bind("exerciseOrder", exerciseOrder)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getExercises(activityId: Int): List<Exercise> =
        handle.createQuery(
            """
            select e.id, e.activity, ge.name, e.exercise_order, ge.url
            from waveCoach.exercise e
            join waveCoach.gym_exercise ge on e.exercise = ge.id
            where e.activity = :activityId
            order by e.exercise_order
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .mapTo<Exercise>()
            .list()

    override fun removeExercisesByAthlete(athleteId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.exercise
            where activity in (
                select id from waveCoach.activity where uid = :athleteId
            )
            """.trimIndent(),
        )
            .bind("athleteId", athleteId)
            .execute()
    }

    override fun removeExercisesByActivity(activityId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.exercise where activity = :activityId
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .execute()
    }

    override fun storeSet(
        exerciseId: Int,
        reps: Int,
        weight: Float,
        rest: Float,
        setOrder: Int,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.set (exercise_id, weight, reps, rest_time, set_order) 
            values (:exercise_id, :weight, :reps, :rest_time, :setOrder)
            """.trimIndent(),
        )
            .bind("exercise_id", exerciseId)
            .bind("weight", weight)
            .bind("reps", reps)
            .bind("rest_time", rest)
            .bind("setOrder", setOrder)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getSets(exerciseId: Int): List<Sets> =
        handle.createQuery(
            """
            select * from waveCoach.set where exercise_id = :exerciseId order by set_order
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .mapTo<Sets>()
            .list()

    override fun removeSetsByAthlete(athleteId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.set 
            where exercise_id in (
                select id from waveCoach.exercise where activity in (
                    select id from waveCoach.activity where uid = :athleteId
                )
            )
            """.trimIndent(),
        )
            .bind("athleteId", athleteId)
            .execute()
    }

    override fun removeSetsByActivity(activityId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.set where exercise_id in (
                select id from waveCoach.exercise where activity = :activityId
            )
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .execute()
    }

    override fun storeGymExercise(
        name: String,
        category: String,
        url: String?,
    ): Int =
        handle.createUpdate(
            """
            insert into waveCoach.gym_exercise (name, category, url) values (:name, :category, :url)
            """.trimIndent(),
        )
            .bind("name", name)
            .bind("category", category)
            .bind("url", url)
            .executeAndReturnGeneratedKeys()
            .mapTo<Int>()
            .one()

    override fun getGymExerciseByName(name: String): GymExercise? =
        handle.createQuery("select * from waveCoach.gym_exercise where name = :name")
            .bind("name", name)
            .mapTo<GymExercise>()
            .findOne()
            .orElse(null)

    override fun getAllGymExercises(): List<GymExercise> =
        handle.createQuery("select * from waveCoach.gym_exercise")
            .mapTo<GymExercise>()
            .list()

    override fun updateGymExercise(
        exerciseId: Int,
        name: String,
        category: String,
    ) {
        handle.createUpdate(
            """
            update waveCoach . gym_exercise set name = :name, category = :category where id = :exerciseId
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .bind("name", name)
            .bind("category", category)
            .execute()
    }

    override fun removeGymExercise(exerciseId: Int) {
        handle.createUpdate("delete from waveCoach.gym_exercise where id = :exerciseId")
            .bind("exerciseId", exerciseId)
            .execute()
    }

    override fun isGymExerciseValid(exerciseId: Int): Boolean =
        handle.createQuery("select exists(select 1 from waveCoach.gym_exercise where id = :exerciseId)")
            .bind("exerciseId", exerciseId)
            .mapTo<Boolean>()
            .one()
}
