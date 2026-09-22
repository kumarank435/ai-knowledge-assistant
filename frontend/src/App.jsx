import { useEffect, useState } from "react";
import "./App.css";

const API_URL = "http://localhost:8081";

function App() {
  // =====================================================
  // DOCUMENT STATE
  // =====================================================

  const [documents, setDocuments] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [selectedDocumentIds, setSelectedDocumentIds] = useState([]);

  // =====================================================
  // CHAT STATE
  // =====================================================

  const [question, setQuestion] = useState("");
  const [messages, setMessages] = useState([]);

  // =====================================================
  // UI STATE
  // =====================================================

  const [uploading, setUploading] = useState(false);
  const [asking, setAsking] = useState(false);

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  // =====================================================
  // LOGIN STATE
  // =====================================================

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loggingIn, setLoggingIn] = useState(false);
  const [loginError, setLoginError] = useState("");

  const token = localStorage.getItem("token");

  // =====================================================
  // ROLE
  // =====================================================

  const getRoleFromToken = (jwtToken) => {
    if (!jwtToken) {
      return null;
    }

    try {
      const payload = JSON.parse(
        atob(jwtToken.split(".")[1])
      );

      return payload.role || null;

    } catch (error) {
      console.error(
        "Unable to decode JWT:",
        error
      );

      return null;
    }
  };

  const role = getRoleFromToken(token);

  const isAdmin = role === "ADMIN";
  const isUser = role === "USER";

  // =====================================================
  // LOAD DOCUMENTS
  // =====================================================

  useEffect(() => {
    if (token) {
      loadDocuments();
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [token]);

  const loadDocuments = async () => {
    try {
      setError("");

      const response = await fetch(
        `${API_URL}/api/documents`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (!response.ok) {
        throw new Error(
          "Failed to load documents"
        );
      }

      const data = await response.json();

      setDocuments(data);

      // Select the first document by default
      if (
        data.length > 0 &&
        selectedDocumentIds.length === 0
      ) {
        setSelectedDocumentIds([
          String(data[0].id)
        ]);
      }

    } catch (err) {
      console.error(err);
      setError(
        "Unable to load documents."
      );
    }
  };

  // =====================================================
  // LOGIN
  // =====================================================

  const handleLogin = async (event) => {
    event.preventDefault();

    if (!username.trim()) {
      setLoginError(
        "Please enter your username."
      );
      return;
    }

    if (!password) {
      setLoginError(
        "Please enter your password."
      );
      return;
    }

    setLoggingIn(true);
    setLoginError("");

    try {
      const response = await fetch(
        `${API_URL}/api/auth/login`,
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/json"
          },

          body: JSON.stringify({
            username: username.trim(),
            password
          })
        }
      );

      const data =
        await response.json();

      if (!response.ok) {
        throw new Error(
          data.message ||
          "Invalid username or password."
        );
      }

      if (!data.token) {
        throw new Error(
          "Login successful, but no token was received."
        );
      }

      localStorage.setItem(
        "token",
        data.token
      );

      window.location.reload();

    } catch (err) {
      console.error(
        "Login error:",
        err
      );

      setLoginError(
        err.message ||
        "Unable to login."
      );

    } finally {
      setLoggingIn(false);
    }
  };

  // =====================================================
  // FILE SELECTION
  // =====================================================

  const handleFileChange = (event) => {
    const file =
      event.target.files[0];

    if (!file) {
      return;
    }

    if (
      file.type !==
      "application/pdf"
    ) {
      setError(
        "Please select a PDF file."
      );

      setSelectedFile(null);

      return;
    }

    setError("");
    setMessage("");
    setSelectedFile(file);
  };

  // =====================================================
  // UPLOAD PDF
  // =====================================================

  const handleUpload = async () => {
    if (!selectedFile) {
      setError(
        "Please select a PDF file first."
      );

      return;
    }

    setUploading(true);
    setError("");
    setMessage("");

    try {
      const formData =
        new FormData();

      formData.append(
        "file",
        selectedFile
      );

      const response =
        await fetch(
          `${API_URL}/api/documents/upload`,
          {
            method: "POST",

            headers: {
              Authorization:
                `Bearer ${token}`
            },

            body: formData
          }
        );

      const data =
        await response.json();

      if (!response.ok) {
        throw new Error(
          data.message ||
          "Upload failed."
        );
      }

      setMessage(
        data.message
      );

      setSelectedFile(null);

      const input =
        document.getElementById(
          "pdfInput"
        );

      if (input) {
        input.value = "";
      }

      await loadDocuments();

    } catch (err) {
      console.error(err);

      setError(
        err.message ||
        "Unable to upload document."
      );

    } finally {
      setUploading(false);
    }
  };

  // =====================================================
  // DELETE DOCUMENT (ADMIN ONLY)
  // =====================================================

  const handleDeleteDocument = async (documentId, documentTitle) => {
    if (!isAdmin) {
      return;
    }

    const confirmed = window.confirm(
      `Are you sure you want to delete "${documentTitle}"?`
    );

    if (!confirmed) {
      return;
    }

    setError("");
    setMessage("");

    try {
      const response = await fetch(
        `${API_URL}/api/documents/${documentId}`,
        {
          method: "DELETE",
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
      );

      if (!response.ok) {
        let errorMessage = "Unable to delete document.";

        try {
          const data = await response.json();
          errorMessage = data.message || errorMessage;
        } catch {
          // Response may have no JSON body.
        }

        throw new Error(errorMessage);
      }

      setDocuments((previousDocuments) =>
        previousDocuments.filter(
          (document) => String(document.id) !== String(documentId)
        )
      );

      setSelectedDocumentIds((previousIds) =>
        previousIds.filter(
          (id) => String(id) !== String(documentId)
        )
      );

      setMessages([]);
      setQuestion("");

      setMessage("Document deleted successfully.");
    } catch (err) {
      console.error("Delete document error:", err);

      setError(
        err.message || "Unable to delete document."
      );
    }
  };

  // =====================================================
  // SELECT / DESELECT DOCUMENT
  // =====================================================

  const selectDocument = (id) => {
    const documentId =
      String(id);

    setSelectedDocumentIds(
      (previousIds) => {

        if (
          previousIds.includes(
            documentId
          )
        ) {
          return previousIds.filter(
            (existingId) =>
              existingId !== documentId
          );
        }

        return [
          ...previousIds,
          documentId
        ];
      }
    );

    // New selection = new conversation
    setMessages([]);
    setQuestion("");
    setError("");
    setMessage("");
  };

  // =====================================================
  // SELECTED DOCUMENTS
  // =====================================================

  const selectedDocuments =
    documents.filter(
      (document) =>
        selectedDocumentIds.includes(
          String(document.id)
        )
    );

  // =====================================================
  // ASK AI
  // =====================================================

  const handleAskQuestion =
    async () => {

      if (
        selectedDocumentIds.length === 0
      ) {
        setError(
          "Please select at least one document."
        );

        return;
      }

      if (!question.trim()) {
        setError(
          "Please enter a question."
        );

        return;
      }

      const documentIds =
        selectedDocumentIds.map(
          (id) => Number(id)
        );

      if (
        documentIds.some(
          (id) =>
            !Number.isInteger(id)
        )
      ) {
        setError(
          "Invalid document selected."
        );

        return;
      }

      const currentQuestion =
        question.trim();

      console.log(
        "========== CHAT REQUEST =========="
      );

      console.log(
        "Document IDs:",
        documentIds
      );

      console.log(
        "Question:",
        currentQuestion
      );

      console.log(
        "=================================="
      );

      setQuestion("");

      setAsking(true);
      setError("");

      const userMessage = {
        id:
          Date.now() +
          "-user",

        type: "user",

        question:
          currentQuestion
      };

      setMessages(
        (previousMessages) => [
          ...previousMessages,
          userMessage
        ]
      );

      try {
        const response =
          await fetch(
            `${API_URL}/api/chat`,
            {
              method: "POST",

              headers: {
                "Content-Type":
                  "application/json",

                Authorization:
                  `Bearer ${token}`
              },

              body: JSON.stringify({
                question:
                  currentQuestion,

                documentIds
              })
            }
          );

        const data =
          await response.json();

        console.log(
          "========== CHAT RESPONSE =========="
        );

        console.log(data);

        console.log(
          "==================================="
        );

        if (!response.ok) {
          throw new Error(
            data.message ||
            "Unable to get answer."
          );
        }

        const assistantMessage = {
          id:
            Date.now() +
            "-assistant",

          type: "assistant",

          answer:
            data.answer || "",

          sources:
            data.sources || []
        };

        setMessages(
          (previousMessages) => [
            ...previousMessages,
            assistantMessage
          ]
        );

      } catch (err) {
        console.error(
          "Chat error:",
          err
        );

        const errorMessage = {
          id:
            Date.now() +
            "-error",

          type: "assistant",

          answer:
            err.message ||
            "Unable to connect to AI.",

          sources: []
        };

        setMessages(
          (previousMessages) => [
            ...previousMessages,
            errorMessage
          ]
        );

        setError(
          err.message ||
          "Unable to connect to AI."
        );

      } finally {
        setAsking(false);
      }
    };

  // =====================================================
  // ENTER TO SEND
  // =====================================================

  const handleKeyDown = (
    event
  ) => {

    if (
      event.key === "Enter" &&
      !event.shiftKey
    ) {
      event.preventDefault();

      if (
        !asking &&
        question.trim()
      ) {
        handleAskQuestion();
      }
    }
  };

  // =====================================================
  // LOGOUT
  // =====================================================

  const handleLogout = () => {
    localStorage.removeItem(
      "token"
    );

    window.location.reload();
  };

  // =====================================================
  // LOGIN SCREEN
  // =====================================================

  if (!token) {

    return (
      <div className="login-page">

        <div className="login-background">

          <div className="login-glow glow-one"></div>

          <div className="login-glow glow-two"></div>

          <div className="login-grid"></div>

        </div>

        <div className="login-card">

          <div className="login-brand">

            <div className="login-logo">
              ✦
            </div>

            <div className="login-brand-text">

              <span>
                KNOWLEDGE
              </span>

              <strong>
                AI
              </strong>

            </div>

          </div>

          <div className="login-content">

            <div className="login-eyebrow">
              PRIVATE AI WORKSPACE
            </div>

            <h1>
              Welcome back.
            </h1>

            <p>
              Your documents are waiting.
              Sign in to continue your
              knowledge workspace.
            </p>

          </div>

          <form
            onSubmit={handleLogin}
            className="login-form"
          >

            <div className="input-group">

              <label>
                Username
              </label>

              <div className="input-wrapper">

                <span className="input-icon">
                  ◉
                </span>

                <input
                  type="text"
                  value={username}
                  onChange={(event) =>
                    setUsername(
                      event.target.value
                    )
                  }
                  placeholder="Enter username"
                  autoComplete="username"
                />

              </div>

            </div>

            <div className="input-group">

              <label>
                Password
              </label>

              <div className="input-wrapper">

                <span className="input-icon">
                  ◈
                </span>

                <input
                  type="password"
                  value={password}
                  onChange={(event) =>
                    setPassword(
                      event.target.value
                    )
                  }
                  placeholder="Enter password"
                  autoComplete="current-password"
                />

              </div>

            </div>

            {loginError && (

              <div className="login-error">

                <span>!</span>

                {loginError}

              </div>

            )}

            <button
              type="submit"
              className="login-button"
              disabled={loggingIn}
            >

              <span>
                {loggingIn
                  ? "Authenticating..."
                  : "Continue to workspace"}
              </span>

              {!loggingIn && (
                <span className="login-arrow">
                  →
                </span>
              )}

            </button>

          </form>

          <div className="login-security">

            <span className="security-dot"></span>

            Secure JWT authentication

          </div>

        </div>

      </div>
    );
  }

  // =====================================================
  // DASHBOARD
  // =====================================================

  return (
    <div className="app">

      {/* =================================================
          SIDEBAR
          ================================================= */}

      <aside className="sidebar">

        <div className="sidebar-top">

          {/* BRAND */}

          <div className="brand">

            <div className="brand-mark">
              ✦
            </div>

            <div className="brand-details">

              <div className="brand-name">
                Knowledge AI
              </div>

              <div className="brand-caption">
                Personal intelligence
              </div>

            </div>

          </div>

          {/* ADMIN-ONLY UPLOAD SECTION */}

          {isAdmin && (
            <div className="new-document-section">

              <label
                className="upload-card"
                htmlFor="pdfInput"
              >

                <div className="upload-icon">
                  +
                </div>

                <div className="upload-text">

                  <strong>
                    {selectedFile
                      ? selectedFile.name
                      : "Add document"}
                  </strong>

                  <span>
                    {selectedFile
                      ? "Ready to process"
                      : "PDF files only"}
                  </span>

                </div>

                <input
                  id="pdfInput"
                  type="file"
                  accept=".pdf,application/pdf"
                  onChange={
                    handleFileChange
                  }
                  hidden
                />

              </label>

              <button
                className="upload-button"
                onClick={
                  handleUpload
                }
                disabled={
                  !selectedFile ||
                  uploading
                }
              >

                {uploading
                  ? "Processing..."
                  : "Upload PDF"}

              </button>

            </div>
          )}

          {/* STATUS */}

          {message && (

            <div className="sidebar-success">
              <span>✓</span>
              {message}
            </div>

          )}

          {error && (

            <div className="sidebar-error">
              <span>!</span>
              {error}
            </div>

          )}

          {/* DOCUMENT HEADER */}

          <div className="documents-header">

            <div className="documents-title">
              YOUR LIBRARY
            </div>

            <div className="documents-count">
              {documents.length}
            </div>

          </div>

          {/* DOCUMENTS */}

          <div className="documents-list">

            {documents.length === 0 ? (

              <div className="empty-library">

                <div className="empty-library-icon">
                  +
                </div>

                <strong>
                  Your library is empty
                </strong>

                <span>
                  Upload a PDF to get started.
                </span>

              </div>

            ) : (

              documents.map(
                (document) => {

                  const active =
                    selectedDocumentIds.includes(
                      String(document.id)
                    );

                  return (

                    <label
                      key={document.id}
                      className={
                        active
                          ? "document-item active"
                          : "document-item"
                      }
                    >

                      {/* CHECKBOX */}

                      <input
                        type="checkbox"
                        className="document-checkbox"
                        checked={active}
                        onChange={() =>
                          selectDocument(
                            document.id
                          )
                        }
                      />

                      {/* PDF ICON */}

                      <div className="document-file-icon">

                        <span>
                          PDF
                        </span>

                      </div>

                      {/* DOCUMENT DETAILS */}

                      <div className="document-details">

                        <strong>
                          {document.title}
                        </strong>

                        <span>
                          Source {document.id}
                        </span>

                      </div>

                      {/* SELECTED TICK */}

                      {active && (

                        <div className="document-active">
                          ✓
                        </div>

                      )}

                      {/* ADMIN-ONLY DELETE */}

                      {isAdmin && (

                        <button
                          type="button"
                          title="Delete document"
                          aria-label={`Delete ${document.title}`}
                          onClick={(event) => {
                            event.preventDefault();
                            event.stopPropagation();

                            handleDeleteDocument(
                              document.id,
                              document.title
                            );
                          }}
                          style={{
                            marginLeft: "8px",
                            border: "none",
                            background: "transparent",
                            cursor: "pointer",
                            fontSize: "18px",
                            lineHeight: 1,
                            padding: "4px",
                            opacity: 0.7
                          }}
                        >
                          ×
                        </button>

                      )}

                    </label>

                  );

                }
              )

            )}

          </div>

        </div>

        {/* SIDEBAR BOTTOM */}

        <div className="sidebar-bottom">

          <div className="profile-card">

            <div className="profile-avatar">
              K
            </div>

            <div className="profile-info">

              <strong>
                My Workspace
              </strong>

              <span>
                Personal account
              </span>

            </div>

          </div>

          <button
            className="logout-button"
            onClick={
              handleLogout
            }
            title="Logout"
          >
            ↪
          </button>

        </div>

      </aside>

      {/* =================================================
          MAIN CONTENT
          ================================================= */}

      <main className="main">

        {/* HEADER */}

        <header className="header">

          <div className="header-left">

            <span className="header-eyebrow">
              KNOWLEDGE WORKSPACE
            </span>

            <h1>

              {selectedDocuments.length === 1
                ? selectedDocuments[0].title
                : selectedDocuments.length > 1
                  ? `${selectedDocuments.length} documents selected`
                  : "Your knowledge base"}

            </h1>

          </div>

          <div className="header-right">

            {selectedDocuments.length > 0 && (

              <div className="active-document">

                <span className="online-dot"></span>

                <span>
                  {selectedDocuments.length === 1
                    ? selectedDocuments[0].fileName
                    : `${selectedDocuments.length} PDFs selected`}
                </span>

              </div>

            )}

            <button
              className="refresh-button"
              onClick={
                loadDocuments
              }
              title="Refresh documents"
            >
              ↻
            </button>

          </div>

        </header>

        {/* CHAT */}

        <section className="chat-area">

          <div className="chat-container">

            {/* EMPTY / WELCOME */}

            {messages.length === 0 && !asking ? (

              <div className="welcome">

                <div className="welcome-orbit">

                  <div className="welcome-icon">
                    ✦
                  </div>

                </div>

                <span className="welcome-eyebrow">
                  YOUR AI RESEARCH ASSISTANT
                </span>

                <h2>
                  Ask your documents
                  <br />
                  <em>anything.</em>
                </h2>

                <p>
                  Search, understand and
                  explore your knowledge base
                  using natural language.
                </p>

                {selectedDocuments.length > 0 ? (

                  <div className="selected-document-card">

                    <div className="selected-file-icon">
                      PDF
                    </div>

                    <div className="selected-file-info">

                      <span>
                        READY TO ANSWER FROM
                      </span>

                      <strong>
                        {selectedDocuments.length === 1
                          ? selectedDocuments[0].fileName
                          : `${selectedDocuments.length} selected documents`}
                      </strong>

                    </div>

                    <div className="selected-check">
                      ✓
                    </div>

                  </div>

                ) : (

                  <div className="select-document-hint">

                    <span>←</span>

                    Select one or more documents
                    from your library
                    to begin

                  </div>

                )}

                {/* SUGGESTION CARDS */}

                {selectedDocuments.length > 0 && (

                  <div className="suggestions">

                    <button
                      onClick={() =>
                        setQuestion(
                          "Summarize the selected documents"
                        )
                      }
                    >
                      <span>✦</span>
                      Summarize the selected documents
                    </button>

                    <button
                      onClick={() =>
                        setQuestion(
                          "What are the key points in the selected documents?"
                        )
                      }
                    >
                      <span>◈</span>
                      Find key points
                    </button>

                    <button
                      onClick={() =>
                        setQuestion(
                          "What are the most important things I should know?"
                        )
                      }
                    >
                      <span>◎</span>
                      What should I know?
                    </button>

                  </div>

                )}

              </div>

            ) : (

              /* =================================================
                 CONVERSATION
                 ================================================= */

              <div className="conversation">

                {messages.map(
                  (chatMessage) => {

                    // USER MESSAGE

                    if (
                      chatMessage.type ===
                      "user"
                    ) {
                      return (

                        <div
                          className="question-row"
                          key={chatMessage.id}
                        >

                          <div className="question-bubble">
                            {chatMessage.question}
                          </div>

                          <div className="user-avatar">
                            K
                          </div>

                        </div>

                      );
                    }

                    // AI MESSAGE

                    return (

                      <div
                        className="answer-row"
                        key={chatMessage.id}
                      >

                        <div className="ai-avatar">
                          ✦
                        </div>

                        <div className="answer-content">

                          <div className="answer-header">

                            <strong>
                              Knowledge AI
                            </strong>

                            <span>
                              Just now
                            </span>

                          </div>

                          <div className="answer-text">
                            {chatMessage.answer}
                          </div>

                          {/* SOURCES */}

                          {chatMessage.sources &&
                            chatMessage.sources.length > 0 && (

                              <div className="sources">

                                <div className="sources-header">

                                  <span>
                                    SOURCES
                                  </span>

                                  <span>
                                    {
                                      chatMessage.sources.length
                                    }{" "}
                                    relevant chunks
                                  </span>

                                </div>

                                <div className="sources-list">

                                  {chatMessage.sources.map(
                                    (
                                      source,
                                      index
                                    ) => (

                                      <div
                                        className="source-card"
                                        key={
                                          `${chatMessage.id}-${index}`
                                        }
                                      >

                                        <div className="source-icon">
                                          PDF
                                        </div>

                                        <div className="source-info">

                                          <strong>
                                            {
                                              source.fileName
                                            }
                                          </strong>

                                          <span>
                                            Retrieved chunk{" "}
                                            {
                                              source.chunkIndex
                                            }
                                          </span>

                                        </div>

                                        <span className="source-arrow">
                                          ↗
                                        </span>

                                      </div>

                                    )
                                  )}

                                </div>

                              </div>

                            )}

                        </div>

                      </div>

                    );
                  }
                )}

                {/* THINKING */}

                {asking && (

                  <div className="thinking-row">

                    <div className="ai-avatar">
                      ✦
                    </div>

                    <div className="thinking-content">

                      <div className="thinking-header">
                        Knowledge AI
                      </div>

                      <div className="thinking-box">

                        <div className="thinking-spinner"></div>

                        <span>
                          Reading your selected documents...
                        </span>

                      </div>

                    </div>

                  </div>

                )}

              </div>

            )}

          </div>

        </section>

        {/* COMPOSER */}

        <footer className="composer">

          <div className="composer-container">

            {/* INPUT */}

            <div className="composer-box">

              <textarea
                value={question}
                onChange={(event) =>
                  setQuestion(
                    event.target.value
                  )
                }
                onKeyDown={
                  handleKeyDown
                }
                placeholder={
                  selectedDocuments.length > 0
                    ? "Ask anything about the selected documents..."
                    : "Select one or more documents to begin..."
                }
                disabled={
                  asking ||
                  selectedDocumentIds.length === 0
                }
              />

              <button
                className="send-button"
                onClick={
                  handleAskQuestion
                }
                disabled={
                  asking ||
                  selectedDocumentIds.length === 0 ||
                  !question.trim()
                }
              >

                {asking
                  ? "..."
                  : "↑"}

              </button>

            </div>

            {/* FOOTER HINT */}

            <div className="composer-footer">

              <span>
                AI answers are grounded
                in your selected documents
              </span>

              <span>
                <kbd>Enter</kbd>
                {" "}
                to send
              </span>

            </div>

          </div>

        </footer>

      </main>

    </div>
  );
}

export default App;