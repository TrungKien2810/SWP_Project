package DAO;

import Model.Comment;
import Model.CommentMedia;
import Model.CommentReply;
import Model.ReplyMedia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommentDB {
    private final DBConnect db = new DBConnect();
    private final Connection conn;
    
    public CommentDB() {
        this.conn = db.conn;
    }
    
    // Verify comment ownership
    public boolean isCommentOwnedBy(int commentId, int userId) {
        String sql = "SELECT 1 FROM Comments WHERE comment_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Delete a comment by owner (also delete media first)
    public boolean deleteCommentByUser(int commentId, int userId) {
        if (!isCommentOwnedBy(commentId, userId)) {
            return false;
        }
        try {
            // delete media first to satisfy FK
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM CommentMedia WHERE comment_id = ?")) {
                ps1.setInt(1, commentId);
                ps1.executeUpdate();
            }
            try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM Comments WHERE comment_id = ? AND user_id = ?")) {
                ps2.setInt(1, commentId);
                ps2.setInt(2, userId);
                return ps2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all comments for a product
    public List<Comment> getCommentsByProductId(int productId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as username, u.avatar_url as avatarUrl " +
                     "FROM Comments c " +
                     "JOIN Users u ON c.user_id = u.user_id " +
                     "WHERE c.product_id = ? " +
                     "ORDER BY c.created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setProductId(rs.getInt("product_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setCommentText(rs.getString("comment_text"));
                    comment.setRating(rs.getInt("rating"));
                    comment.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    comment.setUpdatedAt(rs.getTimestamp("updated_at") != null 
                        ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    comment.setUsername(rs.getString("username"));
                    comment.setAvatarUrl(rs.getString("avatarUrl"));
                    
                    // Get media for this comment
                    List<CommentMedia> mediaList = getMediaByCommentId(comment.getCommentId());
                    comment.setMediaList(mediaList);
                    
                    // Get replies for this comment
                    List<CommentReply> replies = getRepliesByCommentId(comment.getCommentId());
                    comment.setReplies(replies);
                    
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // Get comments with current user's comments prioritized on top
    public List<Comment> getCommentsByProductIdPrioritizeUser(int productId, int userId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as username, u.avatar_url as avatarUrl " +
                     "FROM Comments c " +
                     "JOIN Users u ON c.user_id = u.user_id " +
                     "WHERE c.product_id = ? " +
                     "ORDER BY CASE WHEN c.user_id = ? THEN 0 ELSE 1 END, c.created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setProductId(rs.getInt("product_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setCommentText(rs.getString("comment_text"));
                    comment.setRating(rs.getInt("rating"));
                    comment.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    comment.setUpdatedAt(rs.getTimestamp("updated_at") != null 
                        ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    comment.setUsername(rs.getString("username"));
                    comment.setAvatarUrl(rs.getString("avatarUrl"));
                    List<CommentMedia> mediaList = getMediaByCommentId(comment.getCommentId());
                    comment.setMediaList(mediaList);
                    
                    // Get replies for this comment
                    List<CommentReply> replies = getRepliesByCommentId(comment.getCommentId());
                    comment.setReplies(replies);
                    
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
    
    // Get comments filtered by star rating
    public List<Comment> getCommentsByProductIdAndRating(int productId, int rating) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as username, u.avatar_url as avatarUrl " +
                     "FROM Comments c " +
                     "JOIN Users u ON c.user_id = u.user_id " +
                     "WHERE c.product_id = ? AND c.rating = ? " +
                     "ORDER BY c.created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, rating);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setProductId(rs.getInt("product_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setCommentText(rs.getString("comment_text"));
                    comment.setRating(rs.getInt("rating"));
                    comment.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    comment.setUpdatedAt(rs.getTimestamp("updated_at") != null 
                        ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    comment.setUsername(rs.getString("username"));
                    comment.setAvatarUrl(rs.getString("avatarUrl"));
                    
                    // Get media for this comment
                    List<CommentMedia> mediaList = getMediaByCommentId(comment.getCommentId());
                    comment.setMediaList(mediaList);
                    
                    // Get replies for this comment
                    List<CommentReply> replies = getRepliesByCommentId(comment.getCommentId());
                    comment.setReplies(replies);
                    
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
    
    // Get comments with rating filter and prioritize user's comments
    public List<Comment> getCommentsByProductIdAndRatingPrioritizeUser(int productId, int rating, int userId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.full_name as username, u.avatar_url as avatarUrl " +
                     "FROM Comments c " +
                     "JOIN Users u ON c.user_id = u.user_id " +
                     "WHERE c.product_id = ? AND c.rating = ? " +
                     "ORDER BY CASE WHEN c.user_id = ? THEN 0 ELSE 1 END, c.created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, rating);
            stmt.setInt(3, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Comment comment = new Comment();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setProductId(rs.getInt("product_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setCommentText(rs.getString("comment_text"));
                    comment.setRating(rs.getInt("rating"));
                    comment.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    comment.setUpdatedAt(rs.getTimestamp("updated_at") != null 
                        ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    comment.setUsername(rs.getString("username"));
                    comment.setAvatarUrl(rs.getString("avatarUrl"));
                    
                    // Get media for this comment
                    List<CommentMedia> mediaList = getMediaByCommentId(comment.getCommentId());
                    comment.setMediaList(mediaList);
                    
                    // Get replies for this comment
                    List<CommentReply> replies = getRepliesByCommentId(comment.getCommentId());
                    comment.setReplies(replies);
                    
                    comments.add(comment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
    
    // Get rating distribution for a product
    public int getRatingCount(int productId, int rating) {
        String sql = "SELECT COUNT(*) as count FROM Comments WHERE product_id = ? AND rating = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            stmt.setInt(2, rating);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Get media for a comment
    public List<CommentMedia> getMediaByCommentId(int commentId) {
        List<CommentMedia> mediaList = new ArrayList<>();
        String sql = "SELECT * FROM CommentMedia WHERE comment_id = ? ORDER BY media_order";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CommentMedia media = new CommentMedia();
                    media.setMediaId(rs.getInt("media_id"));
                    media.setCommentId(rs.getInt("comment_id"));
                    media.setMediaUrl(rs.getString("media_url"));
                    media.setMediaType(rs.getString("media_type"));
                    media.setMediaOrder(rs.getInt("media_order"));
                    media.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    mediaList.add(media);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mediaList;
    }
    
    // Add a new comment
    public int addComment(Comment comment) {
        // For SQL Server, use SCOPE_IDENTITY() to get the generated ID
        String sql = "INSERT INTO Comments (product_id, user_id, comment_text, rating, created_at, updated_at) " +
                     "OUTPUT INSERTED.comment_id " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, comment.getProductId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getCommentText());
            stmt.setInt(4, comment.getRating());
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(now));
            stmt.setTimestamp(6, java.sql.Timestamp.valueOf(now));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Add media for a comment
    public boolean addCommentMedia(CommentMedia media) {
        String sql = "INSERT INTO CommentMedia (comment_id, media_url, media_type, media_order, created_at) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, media.getCommentId());
            stmt.setString(2, media.getMediaUrl());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getMediaOrder());
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get average rating for a product
    public double getAverageRating(int productId) {
        String sql = "SELECT AVG(CAST(rating AS FLOAT)) as avg_rating FROM Comments WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("avg_rating");
                    return rs.wasNull() ? 0.0 : avg;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    // Get total comment count for a product
    public int getCommentCount(int productId) {
        String sql = "SELECT COUNT(*) as total FROM Comments WHERE product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // Delete a comment
    public boolean deleteComment(int commentId) {
        String sql = "DELETE FROM Comments WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete media for a comment
    public boolean deleteCommentMedia(int mediaId) {
        String sql = "DELETE FROM CommentMedia WHERE media_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mediaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete all media for a comment
    public boolean deleteAllCommentMedia(int commentId) {
        String sql = "DELETE FROM CommentMedia WHERE comment_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Check if user has purchased product with COMPLETED status
    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM OrderDetails od " +
                     "INNER JOIN Orders o ON od.order_id = o.order_id " +
                     "WHERE o.user_id = ? AND od.product_id = ? AND o.order_status = 'COMPLETED'";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Reply-related methods
    
    // Get all replies for a comment
    public List<CommentReply> getRepliesByCommentId(int commentId) {
        List<CommentReply> replies = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as username, u.avatar_url as avatarUrl " +
                     "FROM CommentReplies r " +
                     "JOIN Users u ON r.user_id = u.user_id " +
                     "WHERE r.comment_id = ? " +
                     "ORDER BY r.created_at ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, commentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CommentReply reply = new CommentReply();
                    reply.setReplyId(rs.getInt("reply_id"));
                    reply.setCommentId(rs.getInt("comment_id"));
                    reply.setUserId(rs.getInt("user_id"));
                    reply.setReplyText(rs.getString("reply_text"));
                    reply.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    reply.setUpdatedAt(rs.getTimestamp("updated_at") != null 
                        ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
                    reply.setUsername(rs.getString("username"));
                    reply.setAvatarUrl(rs.getString("avatarUrl"));
                    
                    // Get media for this reply
                    List<ReplyMedia> mediaList = getMediaByReplyId(reply.getReplyId());
                    reply.setMediaList(mediaList);
                    
                    replies.add(reply);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }
    
    // Add a reply to a comment
    public int addReply(CommentReply reply) {
        String sql = "INSERT INTO CommentReplies (comment_id, user_id, reply_text, created_at, updated_at) " +
                     "OUTPUT INSERTED.reply_id " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            LocalDateTime now = LocalDateTime.now();
            stmt.setInt(1, reply.getCommentId());
            stmt.setInt(2, reply.getUserId());
            stmt.setString(3, reply.getReplyText());
            stmt.setTimestamp(4, java.sql.Timestamp.valueOf(now));
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(now));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Verify reply ownership
    public boolean isReplyOwnedBy(int replyId, int userId) {
        String sql = "SELECT 1 FROM CommentReplies WHERE reply_id = ? AND user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, replyId);
            stmt.setInt(2, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Delete a reply by owner
    public boolean deleteReplyByUser(int replyId, int userId) {
        if (!isReplyOwnedBy(replyId, userId)) {
            return false;
        }
        try {
            // Delete media first to satisfy FK
            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM ReplyMedia WHERE reply_id = ?")) {
                ps1.setInt(1, replyId);
                ps1.executeUpdate();
            }
            
            String sql = "DELETE FROM CommentReplies WHERE reply_id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, replyId);
                stmt.setInt(2, userId);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all media for a reply
    public List<ReplyMedia> getMediaByReplyId(int replyId) {
        List<ReplyMedia> mediaList = new ArrayList<>();
        String sql = "SELECT * FROM ReplyMedia WHERE reply_id = ? ORDER BY media_order ASC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, replyId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReplyMedia media = new ReplyMedia();
                    media.setMediaId(rs.getInt("media_id"));
                    media.setReplyId(rs.getInt("reply_id"));
                    media.setMediaUrl(rs.getString("media_url"));
                    media.setMediaType(rs.getString("media_type"));
                    media.setMediaOrder(rs.getInt("media_order"));
                    media.setCreatedAt(rs.getTimestamp("created_at") != null 
                        ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                    mediaList.add(media);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mediaList;
    }
    
    // Add media to a reply
    public boolean addReplyMedia(ReplyMedia media) {
        String sql = "INSERT INTO ReplyMedia (reply_id, media_url, media_type, media_order, created_at) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, media.getReplyId());
            stmt.setString(2, media.getMediaUrl());
            stmt.setString(3, media.getMediaType());
            stmt.setInt(4, media.getMediaOrder());
            stmt.setTimestamp(5, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

