package com.ticket.ticketmanagement.repository;

import com.ticket.ticketmanagement.entity.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KnowledgeArticleRepository
        extends JpaRepository<KnowledgeArticle, Long> {
}