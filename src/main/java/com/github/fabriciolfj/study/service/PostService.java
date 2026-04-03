package com.github.fabriciolfj.study.service;

import com.github.fabriciolfj.study.projection.PostWithUserRecord;
import com.github.fabriciolfj.study.repositories.PostRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Retorna o Post com seu User (pode ser null).
     * Como Post é uma managed entity dentro do Record, qualquer alteração
     * feita nele dentro de uma transação @Transactional é persistida
     * automaticamente via dirty checking — sem precisar chamar save().
     */
    @Transactional
    public PostWithUserRecord findPostWithUser(Long postId) {
        return postRepository.findPostWithUser(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado: " + postId));
    }

    /**
     * Demonstra o dirty checking funcionando em entidades dentro do Record.
     * O update é feito sem chamar save() — o flush() da transação cuida disso.
     */
    @Transactional
    public PostWithUserRecord updateTitle(Long postId, String newTitle) {
        PostWithUserRecord result = postRepository.findPostWithUser(postId)
                .orElseThrow(() -> new NoSuchElementException("Post não encontrado: " + postId));

        // Post é MANAGED — a mudança de título será detectada pelo dirty checking
        result.post().setTitle(newTitle);

        // Nenhum save() necessário — o flush no fim da transação gera o UPDATE
        return result;
    }
}
