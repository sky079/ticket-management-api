package com.ticket.ticketmanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "knowledge_chunks")
public class KnowledgeChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private KnowledgeArticle article;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(columnDefinition = "vector(768)")
    private float[] embedding;

    public KnowledgeChunk() {
    }

    public Long getId() {
        return id;
    }

    public KnowledgeArticle getArticle() {
        return article;
    }

    public String getContent() {
        return content;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setArticle(KnowledgeArticle article) {
        this.article = article;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}