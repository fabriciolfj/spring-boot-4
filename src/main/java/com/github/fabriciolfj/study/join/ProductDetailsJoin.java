package com.github.fabriciolfj.study.join;

import com.study.details.Detalhes;
import com.study.produto.Produto;
import com.study.produtodetalhes.ProdutoDetalhes;
import org.apache.kafka.streams.kstream.ValueJoiner;

public class ProductDetailsJoin implements ValueJoiner<Produto, Detalhes, ProdutoDetalhes> {

    @Override
    public ProdutoDetalhes apply(Produto produto, Detalhes detalhes) {
        return ProdutoDetalhes.newBuilder()
                .setDescricao(detalhes.getDescricao())
                .setNome(produto.getNome()).build();
    }
}
