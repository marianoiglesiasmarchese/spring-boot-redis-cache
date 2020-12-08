package com.cache.redis.config

import com.cache.redis.model.Gender
import com.cache.redis.model.Student
import com.cache.redis.service.StudentService
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoader: Logging {

    @Bean
    fun addDummyData(
        studentService: StudentService
    ) = CommandLineRunner {
//        val id = UUID.randomUUID().toString()
        val id = (0..15000L).random().toString()
        val student = Student(
            id = id,
            name = "Maria",
            gender = Gender.FEMALE,
            grade = 1
        )
        // saving - log should be performed
        logger().info("######################## SAVE OPERATION #################################")
        var studentResult = studentService.save(student)
        logger().info("Save operation: $studentResult")
        logger().info("#########################################################")

        logger().info("######################## GET OPERATION #################################")
        studentResult = studentService.get(id)
        logger().info("Get operation - Returning Not from cache!: $studentResult")
        studentResult = studentService.get(id)
        logger().info("Get operation - Returning FROM cache!: $studentResult")
        logger().info("#########################################################")

        logger().info("######################### UPDATE OPERATIONS ################################")
        studentResult = studentService.update(student.copy(name = "Mario", gender = Gender.MALE))
        logger().info("Update operation - Returning NOT from cache!: $studentResult")
        studentResult = studentService.get(id)
        logger().info("Get after update operation - Returning FROM cache!: $studentResult")
        logger().info("#########################################################")

    }

}