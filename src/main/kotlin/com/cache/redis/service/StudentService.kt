package com.cache.redis.service

import com.cache.redis.config.Logging
import com.cache.redis.config.logger
import com.cache.redis.model.Student
import com.cache.redis.repository.StudentRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.lang.reflect.Method
import java.util.UUID

@Service
class StudentService(
    val studentRepository: StudentRepository
) : Logging {

    fun save(student: Student): Student {
        logger().info("Save operation performed for student:$student")
        return studentRepository.save(student)
    }

    /**
     * get with result as key instead the default argument key. The condition is required due to a spring issue.
     */
    @Cacheable(value = ["students"], key = "#result", condition = "#result != null")
    fun get(id: String): Student {
        val student = studentRepository.findById(id).orElseThrow { Exception("Student not found") }
        logger().info("Retrieving from DB, student: $student")
        return student!!
    }

    @CachePut(value = ["students"])
    fun update(student: Student): Student {
        logger().info("Update operation performed for student:$student")
        return studentRepository.save(student)
    }

    @CacheEvict(value = ["students"], allEntries = true)
    fun deleteStudentById(student: Student) {
        logger().info("Delete operation performed for student:$student")
        logger().warn("Cache erased!")
        studentRepository.deleteById(student.id)
    }

    /**
     * Caching but skipping irrelevant attribute "txId" from the key [making custom key]
     * @see, https://www.baeldung.com/spring-expression-language
     */
    @Cacheable(value = ["students"], key = "'myPrefix_'.concat(#id)")
    fun cacheWithoutIrrelevantParameterAndCustomKey(txId: UUID, id: String): Student {
        val student = studentRepository.findById(id).orElseThrow { Exception("Student not found") }
        // do something
        logger().info("TxId: $txId, retrieving from DB, student: $student")
        return student!!
    }

    /**
     * Caching choosing object attribute as key
     */
    @CachePut(value = ["students"], key = "#student.name")
    fun updateCustomKey(student: Student): Student {
        logger().info("Update operation performed for student:$student")
        return studentRepository.save(student)
    }

    /**
     * Caching choosing using key generator
     * @see, https://www.baeldung.com/spring-cache-custom-keygenerator
     */
    @CachePut(value = ["students"], keyGenerator = "customKeyGenerator")
    fun updateWithKeyGenerator(student: Student): Student {
        logger().info("Update operation performed for student:$student")
        return studentRepository.save(student)
    }


    /**
     * Cache veto power for those call params who evaluates true
     */
    @CachePut(value = ["students"], unless = "#student.grade > 1000")
    fun updateUnlessParams(student: Student): Student {
        logger().info("Update operation performed for student:$student")
        return studentRepository.save(student)
    }


    /**
     * Cache veto power for those results who evaluates true
     */
    @CachePut(value = ["students"], unless = "#result.grade > 1000")
    fun updateUnlessResult(student: Student): Student {
        logger().info("Update operation performed for student:$student")
        return studentRepository.save(student)
    }

}

class CustomKeyGenerator : KeyGenerator {

    override fun generate(target: Any, method: Method, vararg params: Any?): Any {
        return (target.javaClass.simpleName + "_"
                + method.name + "_"
                + StringUtils.arrayToDelimitedString(params, "_"))
    }
}