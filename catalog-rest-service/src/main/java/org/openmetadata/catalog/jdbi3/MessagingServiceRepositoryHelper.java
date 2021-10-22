/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements. See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openmetadata.catalog.jdbi3;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.openmetadata.catalog.Entity;
import org.openmetadata.catalog.entity.services.MessagingService;
import org.openmetadata.catalog.exception.EntityNotFoundException;
import org.openmetadata.catalog.type.Schedule;
import org.openmetadata.catalog.util.EntityUtil.Fields;
import org.openmetadata.catalog.util.JsonUtils;
import org.openmetadata.catalog.util.ResultList;
import org.openmetadata.catalog.util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.List;

import static org.openmetadata.catalog.exception.CatalogExceptionMessage.entityNotFound;

public class MessagingServiceRepositoryHelper extends EntityRepository<MessagingService> {
  private final MessagingServiceRepository3 repo3;

  public MessagingServiceRepositoryHelper(MessagingServiceRepository3 repo3) {
    super(repo3.messagingServiceDAO());
    this.repo3 = repo3;
  }

  @Transaction
  public List<MessagingService> list(String name) throws IOException {
    return JsonUtils.readObjects(repo3.messagingServiceDAO().list(name), MessagingService.class);
  }

  @Transaction
  public MessagingService get(String id) throws IOException {
    return repo3.messagingServiceDAO().findEntityById(id);
  }

  @Transaction
  public MessagingService getByName(String name) throws IOException {
    return repo3.messagingServiceDAO().findEntityByName(name);
  }

  @Transaction
  public MessagingService create(MessagingService messagingService) throws JsonProcessingException {
    // Validate fields
    Utils.validateIngestionSchedule(messagingService.getIngestionSchedule());
    repo3.messagingServiceDAO().insert(JsonUtils.pojoToJson(messagingService));
    return messagingService;
  }

  @Transaction
  public MessagingService update(String id, String description, List<String> brokers, URI schemaRegistry,
                                 Schedule ingestionSchedule)
          throws IOException {
    Utils.validateIngestionSchedule(ingestionSchedule);
    MessagingService dbService = repo3.messagingServiceDAO().findEntityById(id);
    // Update fields
    dbService.withDescription(description).withIngestionSchedule(ingestionSchedule)
            .withSchemaRegistry(schemaRegistry).withBrokers(brokers);
    repo3.messagingServiceDAO().update(id, JsonUtils.pojoToJson(dbService));
    return dbService;
  }

  @Transaction
  public void delete(String id) {
    if (repo3.messagingServiceDAO().delete(id) <= 0) {
      throw EntityNotFoundException.byMessage(entityNotFound(Entity.MESSAGING_SERVICE, id));
    }
    repo3.relationshipDAO().deleteAll(id);
  }

  @Override
  public String getFullyQualifiedName(MessagingService entity) {
    return null;
  }

  @Override
  public MessagingService setFields(MessagingService entity, Fields fields) throws IOException, ParseException {
    return null;
  }

  @Override
  public ResultList<MessagingService> getResultList(List<MessagingService> entities, String beforeCursor, String afterCursor, int total) throws GeneralSecurityException, UnsupportedEncodingException {
    return null;
  }
}