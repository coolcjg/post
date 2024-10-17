package com.cjg.post.service;

import com.cjg.post.domain.Post;
import com.cjg.post.dto.request.PostListRequestDto;
import com.cjg.post.dto.request.PostSaveRequestDto;
import com.cjg.post.dto.response.PageItem;
import com.cjg.post.dto.response.PostListResponseDto;
import com.cjg.post.dto.response.PostResponseDto;
import com.cjg.post.repository.PostRepository;
import com.cjg.post.util.DateToString;
import com.cjg.post.util.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PostService {

    private final UserService userService;
    private final PostRepository postRepository;
    private final DateToString dateToString;

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

        log.info("---------------");
        log.info("list : " + list);
        log.info("pagination : " + pagination);
        log.info("prevPage : " + prevPage);
        log.info("nextPage : " + nextPage);
        log.info("pageItemList : " + pageItemList);
        log.info("pageNumber : " + (page.getPageable().getPageNumber()+1));
        log.info("totalPage : " + totalPage);
        log.info("totalCount : " + page.getTotalElements());
        log.info("---------------");

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

    public String getQueryParams(PostListRequestDto dto, int pageNumber){

        StringBuilder sb = new StringBuilder();

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

    /*
    public PostLoginResponseDto login(PostLoginRequestDto requestDto){

        Post user = userRepository.findByPostId(requestDto.getPostId());

        if(user == null){
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }else{

            if(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {

                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                grantedAuthorities.add(new SimpleGrantedAuthority(PostRole.USER.getValue()));

                PostDetails userDetails = userDetailsService.loadPostByPostname(requestDto.getPostId());

                PostnamePasswordAuthenticationToken token = new PostnamePasswordAuthenticationToken(userDetails, requestDto.getPassword(), grantedAuthorities);
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String accessToken = jwt.createAccessToken(authentication);
                String refreshToken = jwt.createRefreshToken(authentication);

                return PostLoginResponseDto.builder()
                                .userId(user.getPostId())
                                .name(user.getName())
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();

            }else {
                throw new CustomException(ResultCode.USER_INVALID_PASSWORD);
            }

        }
    }


    @Transactional
    public PostResponseDto modify(PostSaveRequestDto dto){

        Post user = userRepository.findByPostId(dto.getPostId());
        user.setName(aes256.encrypt(dto.getName()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if(dto.getImage() != null){
            String image = user.getImage().substring(user.getImage().lastIndexOf("/")+1);
            s3.deleteImageToS3(image);
            String newImage = s3.upload(dto.getImage());
            user.setImage(newImage);
        }

        return PostResponseDto.builder()
                .userId(dto.getPostId())
                .name(dto.getName())
                .image(user.getImage())
                .build();
    }

    @Transactional
    public void delete(PostDeleteRequestDto dto){
        Post user = userRepository.findByPostId(dto.getPostId());

        if(user != null){
            if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
                s3.deleteImageToS3(user.getImage().substring(user.getImage().lastIndexOf("/")+1));
                userRepository.deleteByPostId(dto.getPostId());
                SecurityContextHolder.clearContext();
            }else{
                throw new CustomException(ResultCode.USER_INVALID_PASSWORD);
            }
        }else{
            throw new CustomException(ResultCode.USER_SEARCH_NOT_FOUND);
        }
    }

     */
}
