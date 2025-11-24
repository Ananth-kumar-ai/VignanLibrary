package org.vignanuniversity.vignanlibrary;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import org.vignanuniversity.vignanlibrary.Book;
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final List<Book> bookList;

    public BookAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);
        holder.title.setText(book.getTitle() == null ? "N/A" : book.getTitle());
        holder.publisher.setText("Publisher: " + (book.getPublisher() == null ? "N/A" : book.getPublisher()));
        holder.edition.setText("Edition: " + (book.getEdition() == null ? "N/A" : book.getEdition()));
        holder.department.setText("Department: " + (book.getDepartment() == null ? "N/A" : book.getDepartment()));
        holder.author.setText("Author: " + (book.getAuthor() == null ? "N/A" : book.getAuthor())); // added
    }

    @Override
    public int getItemCount() {
        return bookList != null ? bookList.size() : 0;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        final TextView title, publisher, edition, department, author;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvTitle);
            publisher = itemView.findViewById(R.id.tvPublisher);
            edition = itemView.findViewById(R.id.tvEdition);
            department = itemView.findViewById(R.id.tvDepartment);
            author = itemView.findViewById(R.id.tvAuthor); // new TextView in layout
        }
    }
}
