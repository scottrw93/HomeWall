/*
 * Copyright 2017-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottw.homewall.controllers;

import com.scottw.homewall.dao.WallsDao;
import com.scottw.homewall.io.UploadWallImage;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/images")
public class ImagesController {
  private final WallsDao wallsDao;

  @Autowired
  public ImagesController(WallsDao wallsDao) {
    this.wallsDao = wallsDao;
  }

  @RequestMapping(path = "/upload", method = RequestMethod.POST)
  @ResponseBody
  public Map<String, String> uploadImage(MultipartFile file)
    throws IOException {
    return Map.of(
      "src",
      UploadWallImage.uploadImage(file.getInputStream(), file.getContentType())
    );
  }
}
