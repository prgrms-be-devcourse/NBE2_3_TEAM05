package edu.example.dev_3_5_cc.repository

import edu.example.dev_3_5_cc.entity.Reply
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.annotation.Commit
import org.springframework.test.context.TestPropertySource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest
@TestPropertySource(locations = ["classpath:application-test.properties"])
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ReplyRepositoryTest {
    @Autowired
    lateinit var replyRepository: ReplyRepository

    @Autowired
    lateinit var memberRepository: MemberRepository

    @Autowired
    lateinit var boardRepository: BoardRepository

    @Test
    @Transactional
    fun testInsert(){
        val reply = Reply().apply {
            val foundBoard = boardRepository.findByIdOrNull(3) ?: throw NoSuchElementException("No found Board")
            val foundMember = memberRepository.findByIdOrNull("user5") ?: throw NoSuchElementException("No found Member")

            board = foundBoard
            member = foundMember
            content = "어그로임 난 개좋음"
        }
        replyRepository.save(reply).run {
            assertNotNull(this)
        }
    }

    @Test
    fun testRead(){
        val replyId = 1L

        val foundReply = replyRepository.findByIdOrNull(replyId) ?: throw NoSuchElementException("No reply found")
        foundReply.run {
            assertEquals(replyId, this.replyId)
        }
    }

    @Test
    @Transactional
    fun testUpdate(){
        val replyId = 1L
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw NoSuchElementException("No reply found")

        reply.apply {
            content = "구라임 나도 걍그럼"
        }

        val foundReply = replyRepository.findByIdOrNull(replyId) ?: throw NoSuchElementException("No reply found")
        foundReply.run {
            assertEquals(content, this.content)
        }
    }

    @Test
    @Transactional
    fun testDelete(){
        val replyId = 5L
        replyRepository.deleteById(replyId)


        assertNull(replyRepository.findByIdOrNull(replyId))
    }

    @Test
    @Transactional
    fun testFindAllByMember(){
        val memberId = "user1"
        val replies = replyRepository.findAllByMember(memberId)

        replies?.forEach {
            assertEquals(memberId, it?.member?.memberId)
        }

        replies?.forEach { reply ->
            println("Reply ID: ${reply?.replyId}, Member ID: ${reply?.member?.memberId}, Board ID: ${reply?.board?.boardId}")
        }

    }
}