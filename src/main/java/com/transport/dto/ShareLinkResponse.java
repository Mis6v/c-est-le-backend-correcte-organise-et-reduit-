package com.transport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShareLinkResponse {

    private String token;

    private String url;
}