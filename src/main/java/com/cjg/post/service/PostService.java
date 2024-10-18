package com.cjg.post.service;

import com.cjg.post.code.ResultCode;
import com.cjg.post.domain.Comment;
import com.cjg.post.domain.CustomUserDetails;
import com.cjg.post.domain.Post;
import com.cjg.post.dto.request.PostDeleteRequestDto;
import com.cjg.post.dto.request.PostListRequestDto;
import com.cjg.post.dto.request.PostModifyRequestDto;
import com.cjg.post.dto.request.PostSaveRequestDto;
import com.cjg.post.dto.response.CommentResponseDto;
import com.cjg.post.dto.response.PageItem;
import com.cjg.post.dto.response.PostListResponseDto;
import com.cjg.post.dto.response.PostResponseDto;
import com.cjg.post.exception.CustomException;
import com.cjg.post.exception.CustomViewException;
import com.cjg.post.repository.PostRepository;
import com.cjg.post.util.AES256;
import com.cjg.post.util.AuthCheck;
import com.cjg.post.util.DateToString;
import com.cjg.post.util.PageUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PostService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final DateToString dateToString;
    private final AES256 aes256;
    private final AuthCheck auth;

    public PostResponseDto save(PostSaveRequestDto dto){

        Post post = Post.builder()
                .user(userService.findByUserId(dto.getUserId()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .open(dto.getOpen().charAt(0))
                .view(0)
                .build();

        Post result = postRepository.save(post);

        return PostResponseDto.builder()
                .postId(result.getPostId())
                .userId(result.getUser().getUserId())
                .title(result.getTitle())
                .content(result.getContent())
                .view(result.getView())
                .open(result.getOpen())
                .regDate(dateToString.apply(result.getRegDate()))
                .modDate(dateToString.apply(result.getModDate()))
                .build();
    }

    public PostListResponseDto list(PostListRequestDto dto){
        Pageable pageable = PageRequest.of(dto.getPageNumber()-1, dto.getPageSize(), Sort.Direction.DESC, "regDate");
        Page<Post> page =  postRepository.list(pageable, dto);

        List<PostResponseDto> list = new ArrayList<>();
        for(Post post : page.getContent()) {
            PostResponseDto temp = PostResponseDto.builder()
                    .postId(post.getPostId())
                    .userId(post.getUser().getUserId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .open(post.getOpen())
                    .view(post.getView())
                    .regDate(dateToString.apply(post.getRegDate()))
                    .modDate(dateToString.apply(post.getModDate()))
                    .build();
            list.add(temp);
        }

        int totalPage = page.getTotalPages() == 0 ? 1 : page.getTotalPages();

        String prevPage = dto.getPageNumber() > 1 ? getQueryParams(dto, dto.getPageNumber()-1) : "";
        String nextPage = dto.getPageNumber() < totalPage ? getQueryParams(dto, dto.getPageNumber()+1) : "";

        List<Integer> pagination = PageUtil.getStartEndPage(dto.getPageNumber(), totalPage);
        List<PageItem> pageItemList = pagination.stream().map(pageNumber-> new PageItem(pageNumber, getQueryParams(dto, pageNumber))).toList();

        return PostListResponseDto.builder()
                .list(list)
                .pageList(pageItemList)
                .prevPage(prevPage)
                .nextPage(nextPage)
                .pageNumber(page.getPageable().getPageNumber()+1)
                .totalPage(totalPage)
                .totalCount(page.getTotalElements())
                .searchType(dto.getSearchType())
                .searchText(dto.getSearchText())
                .build();
    }

    @Transactional
    public PostResponseDto view(CustomUserDetails customUserDetails, Long postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new CustomViewException(ResultCode.POST_SEARCH_NOT_FOUND));
        if(customUserDetails == null || post.getOpen() == 'Y' || (post.getOpen() == 'N' && auth.isSameUserForUser(customUserDetails, post.getUser().getUserId()))){
            post.setView(post.getView()+1);
            return postToDto(post);
        }else{
            throw new CustomViewException(ResultCode.POST_INVALID_AUTH);
        }
    }


    @Transactional
    public PostResponseDto modify(PostModifyRequestDto dto){

        Post post = postRepository.findById(dto.getPostId()).orElseThrow(()->new CustomException(ResultCode.POST_SEARCH_NOT_FOUND));
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setOpen(dto.getOpen().charAt(0));
        post.setModDate(LocalDateTime.now());

        return postToDto(post);
    }

    @Transactional
    public void delete(PostDeleteRequestDto dto){
        postRepository.deleteById(dto.getPostId());
    }

    @Transactional
    public void deleteByUserUserId(String userId){
        postRepository.deleteByUserUserId(userId);
    }

    public String getQueryParams(PostListRequestDto dto, int pageNumber){

        StringBuilder sb = new StringBuilder();
        sb.append("/post/list?");

        if(dto.getSearchType() != null){
            sb.append("searchType=").append(dto.getSearchType()).append("&");
        }

        if(dto.getSearchText() != null){
            sb.append("searchText=").append(dto.getSearchText()).append("&");
        }

        sb.append("pageNumber=").append(pageNumber).append("&");
        sb.append("pageSize=").append(dto.getPageSize()).append("&");

        if(sb.lastIndexOf("&") == sb.length()-1){
            sb.delete(sb.length()-1, sb.length());
        }

        return sb.toString();

    };

    public PostResponseDto postToDto(Post post){
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .name(aes256.decrypt(post.getUser().getName()))
                .image(post.getUser().getImage())
                .title(post.getTitle())
                .content(post.getContent())
                .open(post.getOpen())
                .view(post.getView())
                .commentResponseDtoList(commentListToDto(post.getCommentList()))
                .regDate(dateToString.apply(post.getRegDate()))
                .modDate(dateToString.apply(post.getModDate()))
                .build();
    }

    public List<CommentResponseDto> commentListToDto(List<Comment> commentList){
        return commentList.stream().map(e->CommentResponseDto.builder()
                .commentId(e.getCommentId())
                .parentId(e.getComment() != null ? e.getComment().getCommentId() : 0)
                .postId(e.getPost().getPostId())
                .userId(e.getUser().getUserId())
                .name(aes256.decrypt(e.getUser().getName()))
                .content(e.getContent())
                .deleted(e.getDeleted())
                .regDate(dateToString.apply(e.getRegDate()))
                .build()).toList();
    }
}

