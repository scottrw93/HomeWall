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

import com.scottw.homewall.core.problem.Problem;
import com.scottw.homewall.core.problem.ProblemRequest;
import com.scottw.homewall.dao.ProblemsDao;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
  path = "/problems",
  consumes = MediaType.APPLICATION_JSON_VALUE,
  produces = MediaType.APPLICATION_JSON_VALUE
)
public class ProblemController {
  private final ProblemsDao problemsDao;

  public ProblemController() {
    this.problemsDao = new ProblemsDao();
  }

  @RequestMapping(method = RequestMethod.GET)
  public List<Problem> getProblems() throws IOException {
    return problemsDao.getProblems();
  }

  @RequestMapping(method = RequestMethod.POST)
  public Problem createProblem(@RequestBody ProblemRequest problemRequest) {
    return problemsDao.createProblem(problemRequest);
  }

  @RequestMapping(path = "/{uuid}", method = RequestMethod.DELETE)
  public void deleteProblem(@PathVariable("uuid") UUID uuid) {
    problemsDao.deleteProblem(uuid);
  }
}
