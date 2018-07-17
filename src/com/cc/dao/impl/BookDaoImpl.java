package com.cc.dao.impl;

import com.cc.dao.BookDao;
import com.cc.pojo.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {

    @Override
    public List<Book> queryBookList() {

        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Book> books = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql:///lucene","root","root");
            preparedStatement = conn.prepareStatement("select * from book");
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Book book = new Book();
                book.setId(resultSet.getInt("id"));
                book.setName(resultSet.getString("name"));
                book.setPic(resultSet.getString("pic"));
                book.setPrice(resultSet.getFloat("price"));
                book.setDesc(resultSet.getString("desc"));
                books.add(book);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
}
