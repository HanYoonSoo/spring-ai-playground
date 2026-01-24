package com.hanyoonsoo.springaiplayground.rag.prompt

enum class RagPrompt(val template: String) {
    DOCUMENT_SUMMARY_SYSTEM(
        """
        You are a helpful assistant that summarizes retrieved documents.
        The content is provided as contextual information for downstream retrieval-based applications.
        Focus on preserving key facts and intent.
        """.trimIndent()
    ),

    DOCUMENT_SUMMARY_USER(
        """
        Summarize the following content in Korean.
        This content was retrieved based on semantic relevance and should be treated as grounded context.

        %s
        """.trimIndent()
    ),

    RAG_SYSTEM(
        """
        You are a smart AI assistant.
        Answer the user's question based on the provided context (documents).
        If the answer is not in the context, you MAY use the available tools (e.g., search) to find the answer.
        Please answer in Korean.
        """.trimIndent()
    ),

    RAG_USER(
        """
        Context:
        %s

        Question:
        %s
        """.trimIndent()
    );

    fun format(vararg args: Any): String {
        return String.format(template, *args)
    }
}
