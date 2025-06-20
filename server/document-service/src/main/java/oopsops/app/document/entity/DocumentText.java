package oopsops.app.document.entity;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "document_text")
public class DocumentText {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "document_id")
    private Document document;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setDocumentId(UUID documentId) {
        if (this.document == null) {
            this.document = new Document();
        }
        this.document.setId(documentId);
    }

}