package edu.example.dev_3_5_cc.api_controller

import edu.example.dev_3_5_cc.dto.reply.ReplyListDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyRequestDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyResponseDTO
import edu.example.dev_3_5_cc.dto.reply.ReplyUpdateDTO
import edu.example.dev_3_5_cc.service.ReplyService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/cc/reply")
class ReplyController(
    val replyService: ReplyService
) {
    // Reply 등록 - 확인 완료
    @PostMapping("/")
    fun createReply(@RequestBody replyRequestDTO : ReplyRequestDTO) : ResponseEntity<ReplyResponseDTO> {
        return ResponseEntity.ok(replyService.createReply(replyRequestDTO))
    }

    // Reply 수정 - 확인 완료 *나중에 security 관련만 확인하면 됨
    @PutMapping("/{replyId}")
    @PreAuthorize("@securityUtil.getCurrentUser().memberId == #replyUpdateDTO.memberId or hasRole('ADMIN')")
    fun updateReply(@PathVariable("replyId") replyId : Long,
                    @Validated @RequestBody replyUpdateDTO : ReplyUpdateDTO
    ) : ResponseEntity<ReplyResponseDTO> {
        return ResponseEntity.ok(replyService.updateReply(replyUpdateDTO))
    }

    // Reply 삭제 - 확인 완료
    @DeleteMapping("/{replyId}")
    @PreAuthorize("@replyService.checkDeleteReplyAuthorization(#replyId) or hasRole('ADMIN')") // 댓글 작성자, 보드 작성자 확인
    fun deleteReply(@PathVariable("replyId") replyId : Long) : ResponseEntity<Map<String, String>> {
        replyService.deleteReply(replyId)
        return ResponseEntity.ok(mapOf("message" to "Reply deleted"))
    }

    // Reply 전체 리스트 조회 - 확인 완료
    @GetMapping("/listByMember/{memberId}")
    @PreAuthorize("#memberId == principal.username or hasRole('ADMIN')") // 작성자 확인
    fun listByMemberId(@Validated @PathVariable("memberId") memberId : String) : ResponseEntity<List<ReplyListDTO>> {
        val replies : List<ReplyListDTO> = replyService.listByMemberId(memberId)
        return ResponseEntity.ok(replies)
    }


}