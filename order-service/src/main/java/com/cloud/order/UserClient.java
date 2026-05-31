package com.cloud.order;

import com.cloud.api.ApiResource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping(ApiResource.USERS + ApiResource.USER_BY_ID)
    UserResponse findById(@PathVariable Long id);
}
