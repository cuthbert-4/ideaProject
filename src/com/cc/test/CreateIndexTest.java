package com.cc.test;

import com.cc.dao.BookDao;
import com.cc.dao.impl.BookDaoImpl;
import com.cc.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateIndexTest {

    @Test
    public void test01() throws Exception {

        BookDao bookDao = new BookDaoImpl();
        List<Book> books = bookDao.queryBookList();
        List<Document> documents = new ArrayList<>();
        for (Book book : books) {
            Document document = new Document();
            document.add(new StoredField("id",book.getId().toString()));
            document.add(new TextField("name",book.getName(),Field.Store.YES));
            document.add(new FloatField("price",book.getPrice(),Field.Store.YES));
            document.add(new StoredField("pic",book.getPic()));
            document.add(new TextField("desc",book.getDesc(),Field.Store.NO));
            documents.add(document);

        }
        Analyzer analyzer = new IKAnalyzer();
        Directory directory = FSDirectory.open(new File("D:\\itcast\\lucene\\index"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3,analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,config);
        for (Document document : documents) {
            indexWriter.addDocument(document);
        }
        indexWriter.close();


    }


    @Test
    public void test02() throws Exception {
        Analyzer analyzer = new IKAnalyzer();
        QueryParser queryParser = new QueryParser("desc", analyzer);
        Query query = queryParser.parse("name:编程");
        FSDirectory directory = FSDirectory.open(new File("D:\\itcast\\lucene\\index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 3);
        System.out.println("查询总条数:"+topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc = searcher.doc(docID);
            System.out.println("=============================");
            System.out.println("docID:" + docID);
            System.out.println("bookId:" + doc.get("id"));
            System.out.println("name:" + doc.get("name"));
            System.out.println("price:" + doc.get("price"));
            System.out.println("pic:" + doc.get("pic"));

        }
        indexReader.close();

    }


    @Test
    public void deleteTest() throws Exception{
        Directory directory = FSDirectory.open(new File("D:\\itcast\\lucene\\index"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);
        indexWriter.deleteDocuments(new Term("name","lucene"));
        indexWriter.commit();
        indexWriter.close();

    }

    @Test
    public void updateTest() throws Exception{
        Directory directory = FSDirectory.open(new File("D:\\itcast\\lucene\\index"));
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);
        Document document = new Document();
        document.add(new StoredField("id","傻姑宝爸"));
        document.add(new TextField("name","哈哈哈哈",Field.Store.YES));
        indexWriter.updateDocument(new Term("name","spring"),document);
        indexWriter.commit();
        indexWriter.close();



    }


    @Test
    public void queryTerm() throws Exception{
        TermQuery query = new TermQuery(new Term("name", "编程"));
        doSearch(query);

        Query query2 = NumericRangeQuery.newFloatRange("price",55f,90f,true,true);
        doSearch(query2);

        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(query,BooleanClause.Occur.MUST_NOT);
        booleanQuery.add(query2,BooleanClause.Occur.MUST);
        doSearch(booleanQuery);


    }

    public void doSearch(Query query) throws Exception{
        Analyzer analyzer = new IKAnalyzer();
        FSDirectory directory = FSDirectory.open(new File("D:\\itcast\\lucene\\index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        TopDocs topDocs = searcher.search(query, 3);
        System.out.println("查询总条数:"+topDocs.totalHits);
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            int docID = scoreDoc.doc;
            Document doc = searcher.doc(docID);
            System.out.println("=============================");
            System.out.println("docID:" + docID);
            System.out.println("bookId:" + doc.get("id"));
            System.out.println("name:" + doc.get("name"));
            System.out.println("price:" + doc.get("price"));
            System.out.println("pic:" + doc.get("pic"));

        }
        indexReader.close();
    }




}
