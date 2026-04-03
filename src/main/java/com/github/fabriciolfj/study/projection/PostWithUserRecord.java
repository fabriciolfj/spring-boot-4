package com.github.fabriciolfj.study.projection;

import com.github.fabriciolfj.study.entity.Post;
import com.github.fabriciolfj.study.entity.UserPost;

/**
 * Projeção baseada em Java Record.
 *
 * Ponto chave: mesmo sendo um "DTO", as entidades internas (Post e User)
 * continuam MANAGED pelo Persistence Context — dirty checking funciona
 * normalmente. Isso diferencia essa abordagem de um DTO puro.
 *
 * O User pode ser null quando não há PostDetails ou quando o PostDetails
 * não tem um User associado.
 */
public record PostWithUserRecord(Post post, UserPost user) {}
