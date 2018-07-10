# InformationRetrieval_Project

## PROJECT DESCRIPTION
### Goal: 
Design and build your information retrieval systems, evaluate and compare their performance levels in terms of retrieval effectiveness
With these goals in mind, we designed different versions of information retrieval systems with various models (BM25, Cosine Similarity, Lucene, TF-IDF), processing (stemming, stopping) and evaluated their performances based on precision and recall values. This project encompasses the core information retrieval concepts and processes learnt throughout the semester resulting in building our very own search engines. We used the Cosine Similarity model as our baseline run and Pseudo Relevance feedback as our query expansion technique.

#### Baseline
We have used the Cosine Similarity model as the base search engine because we found the results to be most efficient and consistent when compared with results from “cacm.rel”.

#### Query Expansion
The technique used for query expansion was Pseudo relevance feedback. We preferred this technique over the others because based on a research performed by Hull, D. A. on Stemming algorithms - a case study for detailed evaluation. Journal of the American Society for Information Science it stated that this approach is an efficient and less time-consuming and reduces load on the user. Many expansion terms identified in traditional approaches are indeed unrelated to the query and harmful to the retrieval. Hence we decided to proceed with this approach.

#### Tools
The tools we used for this project are Eclipse-Neon, Eclipse-Mars, notepad++ to compare results and Microsoft excel to display results. We have also used Lucene for its default and BM25 implementation. The citations for more resources used for research as given at the end of this document.

