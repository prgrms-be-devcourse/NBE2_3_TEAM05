package edu.example.dev_3_5_cc.service

import edu.example.dev_3_5_cc.dto.reply.ReplyListDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyResponseDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.entity.Reply
import edu.example.dev_3_5_cc.exception.BoardException
import edu.example.dev_3_5_cc.exception.JWTException
import edu.example.dev_3_5_cc.exception.MemberException
import edu.example.dev_3_5_cc.exception.ReplyException
import edu.example.dev_3_5_cc.repository.BoardRepository
import edu.example.dev_3_5_cc.repository.MemberRepository
import edu.example.dev_3_5_cc.repository.ReplyRepository
import edu.example.dev_3_5_cc.util.SecurityUtil
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service


@Service
@Transactional
class ReplyService(
    private val replyRepository: ReplyRepository,
    private val memberRepository: MemberRepository, // 얘네는 필요없는 것 같긴 한데 나중에 수정하면서 필요할 수도 있을 것 같아 냅뒀습니다.
    private val boardRepository: BoardRepository,   // 끝까지 필요없으면 나중에 지울 예정입니다
    private val securityUtil: SecurityUtil
) {

    fun createReply(replyRequestDTO: ReplyRequestDTO) : ReplyResponseDTO{
        val member = memberRepository.findByIdOrNull(replyRequestDTO.memberId) ?: throw MemberException.NOT_FOUND.get()
        val board = boardRepository.findByIdOrNull(replyRequestDTO.boardId) ?: throw BoardException.NOT_FOUND.get()

        val reply = replyRequestDTO.toEntity(member, board)

        val savedReply = replyRepository.save(reply)
        return ReplyResponseDTO(savedReply)
    }

    // 이거 2차 프로젝트에도 통째로 주석처리 돼있길래 해뒀습니다
//    fun readReply(replyId : Long) : ReplyResponseDTO{
//        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()
//
//        return ReplyResponseDTO(reply)
//    }

    fun updateReply(replyUpdateDTO: ReplyUpdateDTO) : ReplyResponseDTO{
        val reply = replyRepository.findByIdOrNull(replyUpdateDTO.replyId) ?: throw ReplyException.NOT_FOUND.get()

        try {
            with(reply){
                content = replyUpdateDTO.content
            }
        }catch(e: Exception){
            throw ReplyException.NOT_UPDATED.get()
        }

        return ReplyResponseDTO(reply)
    }

    fun deleteReply(replyId : Long){
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()

        try {
            replyRepository.delete(reply)
        }catch(e: Exception){
            throw ReplyException.NOT_DELETED.get()
        }
    }

    fun listByMemberId(memberId: String): List<ReplyListDTO>{
        val replies : List<Reply> = replyRepository.findAllByMember(memberId)

        if(replies.isEmpty()) throw ReplyException.NOT_FOUND.get()

        return replies.map { ReplyListDTO(it) }
    }

    fun checkDeleteReplyAuthorization(replyId : Long) : Boolean{
        val reply = replyRepository.findByIdOrNull(replyId) ?: throw ReplyException.NOT_FOUND.get()
        val currentUser = securityUtil.getCurrentUser()

        // 관리자이거나, 댓글 작성자이거나, 해당 댓글이 달린 게시판의 작성자인지 확인
        if (currentUser.memberId.equals(reply.member?.memberId) ||
            currentUser.memberId.equals(
                reply.board?.member?.memberId
            ) || "ROLE_ADMIN" == securityUtil.getCurrentUserRole()
        ) {
            return true
        }
        // 원래에서는 AccessDeniedException이 있는데 이걸 만들려고 보니 자꾸 오류가 나서 그냥 저렇게 해뒀습니다
        // 권한이 없는 경우 예외 발생
        throw JWTException("권한이 없습니다")
    }


}