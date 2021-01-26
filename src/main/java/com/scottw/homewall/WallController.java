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

package com.scottw.homewall;

import com.scottw.homewall.core.wall.Wall;
import com.scottw.homewall.core.wall.WallRequest;
import com.scottw.homewall.dao.WallsDao;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
  value = "/walls",
  consumes = MediaType.APPLICATION_JSON_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE
)
public class WallController {
  private final WallsDao wallsDao;

  public WallController() {
    this.wallsDao = new WallsDao();
  }

  @RequestMapping(method = RequestMethod.POST)
  public Wall createWall(@RequestBody WallRequest wallRequest) {
    return wallsDao.createWall(wallRequest);
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<Wall> getWalls() {
    return wallsDao.getWalls();
  }
}
