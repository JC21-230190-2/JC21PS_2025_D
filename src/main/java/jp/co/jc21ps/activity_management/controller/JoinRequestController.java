package jp.co.jc21ps.activity_management.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jp.co.jc21ps.activity_management.dto.JoinRequestDto;
import jp.co.jc21ps.activity_management.dto.JoinRequestSaveDto;
import jp.co.jc21ps.activity_management.dto.SessionDto;
import jp.co.jc21ps.activity_management.form.JoinRequestForm;
import jp.co.jc21ps.activity_management.form.JoinRequestSaveForm;
import jp.co.jc21ps.activity_management.service.CommonService;
import jp.co.jc21ps.activity_management.service.JoinRequestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/joinRequest")
public class JoinRequestController {

    private final JoinRequestService joinRequestService;
    private final MessageSource messageSource;
    private final CommonService commonService;

    public JoinRequestController(JoinRequestService joinRequestService,
                                 MessageSource messageSource,
                                 CommonService commonService) {
        this.joinRequestService = joinRequestService;
        this.messageSource = messageSource;
        this.commonService = commonService;
    }

    /**
     * ① 初期表示（一覧画面）
     */
    @GetMapping
    public ModelAndView getJoinRequestList(
            HttpSession session,
            @ModelAttribute("joinOkMessage") String joinOkMessage) {

        ModelAndView mav = new ModelAndView();

        // セッション情報
        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();
        String leaderClubId = sessionDto.getClubId();

        if (userId == null || userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // DTO準備
        JoinRequestDto dto = new JoinRequestDto();
        dto.setUserId(userId);

        // 一覧取得
        List<JoinRequestDto> dtoList = joinRequestService.findRequest(dto);

        List<JoinRequestForm> formList = new ArrayList<>();
        for (JoinRequestDto data : dtoList) {
            JoinRequestForm f = new JoinRequestForm();
            f.setClubId(data.getClubId());
            f.setClubName(data.getClubName());
            f.setClubDescription(data.getClubDescription());
            formList.add(f);
        }

        // 申請する部署がない場合のメッセージ
        String notRequestClubMessage = null;
        if (formList.isEmpty()) {
            notRequestClubMessage = messageSource.getMessage("notRequestClubMessage", null, Locale.getDefault());
        }

        mav.addObject("joinRequestSaveForm", formList);
        mav.addObject("joinRequestCompleteMessage", joinOkMessage);
        mav.addObject("notRequestClubMessage", notRequestClubMessage);
        mav.addObject("leaderClubId", leaderClubId);

        mav.setViewName("joinRequest");
        return mav;
    }

    /**
     * ② 確認画面表示
     */
    @PostMapping("/confirm")
    public ModelAndView confirmJoinRequest(
            HttpSession session,
            JoinRequestForm paramForm) {

        ModelAndView mav = new ModelAndView();

        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();

        if (userId == null || userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // 対象データをフォームのまま渡す
        mav.addObject("confirmData", paramForm);
        mav.setViewName("joinRequestConfirm");
        return mav;
    }

    /**
     * ③ DB登録処理
     */
    @PostMapping("/save")
    public ModelAndView insertRequestClub(
            HttpSession session,
            JoinRequestSaveForm paramForm,
            RedirectAttributes redirectAttributes) {

        ModelAndView mav = new ModelAndView();

        SessionDto sessionDto = commonService.getSessionDto(session);
        String userId = sessionDto.getUserId();

        if (userId == null || userId.isEmpty()) {
            mav.setViewName("error");
            return mav;
        }

        // DTO にセット
        JoinRequestSaveDto dto = new JoinRequestSaveDto();
        dto.setUserId(userId);
        dto.setClubId(paramForm.getClubId());

        boolean result = joinRequestService.insertJoinRequest(dto);

        if (result) {
            // 成功メッセージ
            String successMessage = messageSource.getMessage(
                    "joinRequestCompleteMessage", null, Locale.getDefault());
            redirectAttributes.addFlashAttribute("joinOkMessage", successMessage);

            mav.setViewName("redirect:/joinRequest");
        } else {
            // 失敗メッセージ
            mav.setViewName("error");
        }

        return mav;
    }
}
