package com.oriole.wisepen.resource.mq;

import com.oriole.wisepen.document.api.domain.mq.DocumentReadyMessage;
import com.oriole.wisepen.note.api.domain.mq.NoteSnapshotMessage;
import com.oriole.wisepen.resource.constant.SearchConstants;
import com.oriole.wisepen.resource.service.ISearchSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.oriole.wisepen.document.api.constant.MqTopicConstants.TOPIC_DOCUMENT_READY;
import static com.oriole.wisepen.note.api.constant.MqTopicConstants.TOPIC_NOTE_SNAPSHOT;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceContentConsumer {

    private final ISearchSyncService searchSyncService;

    @KafkaListener(topics = TOPIC_DOCUMENT_READY, groupId = "wisepen-document-ready-group")
    public void onDocumentReady(DocumentReadyMessage message) {
        log.info("documentReady received topic={} resourceId={} contentLength={}",
                TOPIC_DOCUMENT_READY, message.getResourceId(), message.getContent()!=null ? message.getContent().length() : 0);
        try {
            searchSyncService.syncResourceContent(message.getResourceId(), message.getContent());
        } catch (Exception e) {
            log.error("documentReady consume failed topic={} resourceId={}", TOPIC_DOCUMENT_READY, message.getResourceId(), e);
        }
    }

    @KafkaListener(topics = TOPIC_NOTE_SNAPSHOT, groupId = "wisepen-note-snapshot-group")
    public void onNoteSnapshot(NoteSnapshotMessage message) {
        log.info("noteSnapshot received topic={} resourceId={} contentLength={}",
                TOPIC_NOTE_SNAPSHOT, message.getResourceId(), message.getPlainText()!=null ? message.getPlainText().length() : 0);

        try {
            if ("FULL".equals(message.getType())) {
                searchSyncService.syncResourceContent(message.getResourceId(), message.getPlainText());
            }
        } catch (Exception e) {
            log.error("documentReady consume failed topic={} resourceId={}", TOPIC_NOTE_SNAPSHOT, message.getResourceId(), e);
        }
    }
}
