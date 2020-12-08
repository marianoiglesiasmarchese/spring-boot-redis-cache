package com.cache.redis.model

import org.hibernate.annotations.NotFound
import java.io.Serializable
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class Student(
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: String,
    @NotFound
    var name: String,
    @Enumerated(EnumType.STRING)
    var gender: Gender,
    val grade: Long
) : Serializable

enum class Gender {
    MALE,
    FEMALE
}

