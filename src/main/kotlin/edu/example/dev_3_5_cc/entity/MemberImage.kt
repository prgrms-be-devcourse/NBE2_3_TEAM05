package edu.example.dev_3_5_cc.entity

import jakarta.persistence.Embeddable

@Embeddable
data class MemberImage (
    var filename: String? = null
)