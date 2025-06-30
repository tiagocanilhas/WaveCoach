package waveCoach.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import waveCoach.domain.*
import waveCoach.repository.GymActivityRepository

class JdbiGymActivityRepository(
    private val handle: Handle,
) : GymActivityRepository {
    // Activity methods
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

    // Exercise methods
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

    override fun storeExercises(exercises: List<ExerciseToInsert>): List<Int> =
        handle.prepareBatch(
            """
            insert into waveCoach.exercise (activity, exercise, exercise_order) 
            values (:activity, :exercise, :exerciseOrder)
            """
        ).use { batch ->
            exercises.forEach { exercise ->
                batch.bind("activity", exercise.activityId)
                    .bind("exercise", exercise.gymExerciseId)
                    .bind("exerciseOrder", exercise.order)
                    .add()
            }
            batch.executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .list()
        }

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

    override fun getExerciseById(exerciseId: Int): Exercise? =
        handle.createQuery(
            """
            select e.id, e.activity, ge.name, e.exercise_order, ge.url
            from waveCoach.exercise e
            join waveCoach.gym_exercise ge on e.exercise = ge.id
            where e.id = :exerciseId
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .mapTo<Exercise>()
            .singleOrNull()

    override fun updateExercises(exercises: List<ExerciseToUpdate>) {
        handle.prepareBatch(
            """
            update waveCoach.exercise set exercise_order = coalesce(:exerciseOrder, exercise_order)
            where id = :id
            """
        ).use { batch ->
            exercises.forEach { exercise ->
                batch
                    .bind("id", exercise.id)
                    .bind("exerciseOrder", exercise.order)
                    .add()
            }
            batch.execute()
        }
    }

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

    override fun removeExerciseById(exerciseId: Int) {
        handle.createUpdate(
            """
            delete from waveCoach.exercise where id = :exerciseId
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .execute()
    }

    override fun removeExercisesById(exerciseIds: List<Int>) {
        handle.prepareBatch("delete from waveCoach.exercise where id = :exerciseId")
            .use { batch ->
                exerciseIds.forEach { exerciseId ->
                    batch.bind("exerciseId", exerciseId).add()
                }
                batch.execute()
            }
    }

    // Set methods
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

    override fun storeSets(sets: List<SetToInsert>): List<Int> =
        handle.prepareBatch(
            """
            insert into waveCoach.set (exercise_id, weight, reps, rest_time, set_order) 
            values (:exercise_id, :weight, :reps, :rest_time, :setOrder)
            """
        ).use { batch ->
            sets.forEach { set ->
                batch.bind("exercise_id", set.exerciseId)
                    .bind("weight", set.weight)
                    .bind("reps", set.reps)
                    .bind("rest_time", set.restTime)
                    .bind("setOrder", set.setOrder)
                    .add()
            }
            batch.executeAndReturnGeneratedKeys()
                .mapTo<Int>()
                .list()
        }

    override fun getSets(exerciseId: Int): List<Sets> =
        handle.createQuery(
            """
            select * from waveCoach.set where exercise_id = :exerciseId order by set_order
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .mapTo<Sets>()
            .list()

    override fun getSetById(setId: Int): Sets? =
        handle.createQuery(
            """
            select * from waveCoach.set where id = :setId
            """.trimIndent(),
        )
            .bind("setId", setId)
            .mapTo<Sets>()
            .singleOrNull()

    override fun updateSets(sets: List<SetToUpdate>) {
        handle.prepareBatch(
            """
        update waveCoach.set 
        set 
            weight = coalesce(:weight, weight), 
            reps = coalesce(:reps, reps), 
            rest_time = coalesce(:restTime, rest_time), 
            set_order = coalesce(:setOrder, set_order) 
        where id = :id
        """
        ).use { batch ->
            sets.forEach { set ->
                batch.bind("id", set.id)
                    .bind("weight", set.weight)
                    .bind("reps", set.reps)
                    .bind("restTime", set.restTime)
                    .bind("setOrder", set.order)
                    .add()
            }
            batch.execute()
        }
    }

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

    override fun removeSetById(setId: Int) {
        handle.createUpdate("delete from waveCoach.set where id = :setId")
            .bind("setId", setId)
            .execute()
    }

    override fun removeSetsById(setIds: List<Int>) {
        handle.prepareBatch("delete from waveCoach.set where id = :setId")
            .use { batch ->
                setIds.forEach { setId ->
                    batch.bind("setId", setId).add()
                }
                batch.execute()
            }
    }

    // Verify methods
    override fun verifyExerciseOrder(activityId: Int, exerciseOrder: Int): Boolean =
        handle.createQuery(
            """
            select exists(select 1 from waveCoach.exercise where activity = :activityId and exercise_order = :exerciseOrder)
            """.trimIndent(),
        )
            .bind("activityId", activityId)
            .bind("exerciseOrder", exerciseOrder)
            .mapTo<Boolean>()
            .one()

    override fun setBelongsToExercise(exerciseId: Int, setId: Int): Boolean =
        handle.createQuery(
            """
            select exists(select 1 from waveCoach.set where exercise_id = :exerciseId and id = :setId)
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .bind("setId", setId)
            .mapTo<Boolean>()
            .one()

    override fun verifySetOrder(exerciseId: Int, setOrder: Int): Boolean =
        handle.createQuery(
            """
            select exists(select 1 from waveCoach.set where exercise_id = :exerciseId and set_order = :setOrder)
            """.trimIndent(),
        )
            .bind("exerciseId", exerciseId)
            .bind("setOrder", setOrder)
            .mapTo<Boolean>()
            .one()

    // GymExercise methods

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
