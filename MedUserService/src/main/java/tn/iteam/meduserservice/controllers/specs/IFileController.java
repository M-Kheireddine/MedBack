package tn.iteam.meduserservice.controllers.specs;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tn.iteam.meduserservice.utils.ApiUtils;

@RequestMapping
public interface IFileController {
    @GetMapping(ApiUtils.API_GET_PROFILE_IMAGE)
    ResponseEntity<byte[]> getProfileImage(@PathVariable("userId") String userId);
}
