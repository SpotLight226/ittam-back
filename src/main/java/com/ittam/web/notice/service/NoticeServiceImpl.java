package com.ittam.web.notice.service;

import com.ittam.web.aws.S3Uploader;
import com.ittam.web.command.NoticeImgVO;
import com.ittam.web.command.NoticeSearchVO;
import com.ittam.web.command.NoticeVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Service("noticeService")
public class NoticeServiceImpl  implements NoticeService{

    private final S3Uploader s3Uploader;

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public ArrayList<NoticeVO> getNotice() {
        return noticeMapper.getNotice();
    }

    @Override
    public NoticeVO getDetail(String id) {
        return noticeMapper.getDetail(id);
    }

    @Override
    public NoticeImgVO getDetailImg(String id){return noticeMapper.getDetailImg(id);};


    @Override
    public void postNotice(NoticeVO noticeVO, List<MultipartFile> multipartFiles) {

        noticeMapper.postNoticeSingle(noticeVO);

        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

        List<NoticeImgVO> noticeImgVOList;

        if(!multipartFiles.isEmpty()){
            noticeImgVOList = s3Uploader.uploadImage(multipartFiles);
            noticeImgVOList.forEach(noticeImgVO -> {
                noticeImgVO.setNotice_num2(noticeVO.getNotice_num());
                noticeImgVO.setNoticeimg_regdate(currentTimestamp);
            });

            noticeMapper.postNoticeImgList(noticeImgVOList);
        }



//        Map<String, Object> params = new HashMap<>();
//        params.put("notice", noticeVO);
//        params.put("noticeImgList", noticeImgVOList);
//        noticeMapper.postNotice(params);
    }

    @Override
    public void updateNotice(NoticeVO noticeVO, List<MultipartFile> multipartFiles) {
        noticeMapper.updateNotice(noticeVO);

        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());

        if (!multipartFiles.isEmpty()) {
            // 이미지 파일이 있는 경우, 업로드 및 업데이트
            List<NoticeImgVO> noticeImgVOList = s3Uploader.uploadImage(multipartFiles);
            noticeImgVOList.forEach(noticeImgVO -> {
                noticeImgVO.setNotice_num2(noticeVO.getNotice_num());
                noticeImgVO.setNoticeimg_regdate(currentTimestamp);
                System.out.println(noticeImgVO);
            });
            System.out.println("==================");
            System.out.println(noticeImgVOList.get(0).getNotice_num2());
            noticeMapper.updateNoticeImg(noticeImgVOList.get(0));
        } else {
            // 이미지 파일이 없는 경우
            // 여기에서 기존 이미지를 삭제하거나, 새 이미지를 추가하거나 할 작업을 수행합니다.
            // 예시로 기존 이미지를 삭제하고 새 이미지를 추가하는 코드를 보여드리겠습니다.

            // 기존 이미지 삭제
            NoticeImgVO noticeImgVOToDelete = new NoticeImgVO();
            noticeImgVOToDelete.setNotice_num2(noticeVO.getNotice_num());
            noticeMapper.deleteNoticeImg(noticeImgVOToDelete);

            // 새 이미지 추가
            List<NoticeImgVO> noticeImgVOListToAdd = s3Uploader.uploadImage(multipartFiles);
            noticeImgVOListToAdd.forEach(noticeImgVO -> {
                noticeImgVO.setNotice_num2(noticeVO.getNotice_num());
                noticeImgVO.setNoticeimg_regdate(currentTimestamp);
            });

            // 이미지를 추가합니다.
            noticeMapper.postNoticeImgList(noticeImgVOListToAdd);
        }
    }



    @Override
    public void deleteNotice(NoticeVO noticeVO) {
        noticeMapper.deleteNotice(noticeVO);
    }

    @Override
    public void deleteNoticeImg(NoticeImgVO noticeimgVO) {
        noticeMapper.deleteNoticeImg(noticeimgVO);
    }

    @Override
    public NoticeVO getNoticeOne(Long notice_num) {
        return noticeMapper.getNoticeOne(notice_num);
    }

    @Override
    public List<NoticeVO> searchNoticeByTitle(String title) {
        return noticeMapper.searchNoticeByTitle(title);
    }

    @Override
    public List<NoticeVO> searchNoticeByContent(String content) {
        return noticeMapper.searchNoticeByContent(content);
    }

    @Override
    public List<NoticeVO> searchNoticeIsActive() {
        return noticeMapper.searchNoticeIsActive();
    }

    @Override
    public List<NoticeVO> searchNoticeIsExpire() {
        return noticeMapper.searchNoticeIsExpire();
    }

    @Override
    public List<NoticeVO> searchNoticeByDate(NoticeSearchVO noticeSearchVO) {
        return noticeMapper.searchNoticeByDate(noticeSearchVO);
    }

    @Override
    public void upperNoticeHits(String notice_num) {
        noticeMapper.upperNoticeHits(notice_num);
    }


}
