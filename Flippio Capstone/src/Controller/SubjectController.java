package Controller;

import FileManager.SubjectFileManager;
import Model.Subject;
import View.TeacherSubjectDashboardView;
import Exception.FileReadException;

import java.util.ArrayList;
import java.util.List;

public class SubjectController {
    private final SubjectFileManager fileManager;
    private final TeacherSubjectDashboardView view;
    private List<Subject> subjects = new ArrayList<>();

    private Model.Teacher teacher;

    public SubjectController(SubjectFileManager fileManager, TeacherSubjectDashboardView view) {
        this.fileManager = fileManager;
        this.view = view;
        this.view.onAddSubject(this::addSubject);
        this.view.onRemoveSubject(this::removeSelectedSubject);
        this.view.onOpenSubject(this::openSubjectDashboard);
        loadSubjects();
    }

    private void loadSubjects() {
        try {
            subjects = fileManager.readAll();
            view.setSubjects(subjects);
        } catch (FileReadException e) {
            view.showError("Failed to load subjects: " + e.getMessage());
        }
    }

    private void addSubject(String id, String name) {
        String tId = this.teacher != null ? this.teacher.getIdNumber() : null;
        
        // Use new teacher-scoped storage
        if (tId != null) {
            try {
                FileManager.TeacherSubjectFileManager tsfm = new FileManager.TeacherSubjectFileManager(tId);
                // Check if subject already exists for this teacher
                if (tsfm.subjectExists(id)) {
                    view.showError("Subject ID " + id + " already exists in your account.");
                    return;
                }
                // Create new subject with empty student list
                tsfm.saveSubject(id, name, new ArrayList<>());
                subjects.add(new Subject(id, name, tId));
                
                // Update view with fresh data from teacher file
                try {
                    java.util.List<String[]> summaries = new java.util.ArrayList<>();
                    for (Subject s : subjects) {
                        try {
                            FileManager.TeacherSubjectFileManager tempTsfm = new FileManager.TeacherSubjectFileManager(tId);
                            FileManager.TeacherSubjectFileManager.SubjectData data = tempTsfm.getSubject(s.getId());
                            int count = data != null ? data.getStudentCount() : 0;
                            summaries.add(new String[]{s.getId(), s.getName(), String.valueOf(count)});
                        } catch (Exception ex) {
                            summaries.add(new String[]{s.getId(), s.getName(), "0"});
                        }
                    }
                    view.setSubjectSummaries(summaries);
                } catch (Exception e) {
                    view.setSubjects(subjects);
                }
            } catch (Exception e) {
                view.showError("Failed to add subject: " + e.getMessage());
            }
        } else {
            // Legacy fallback
            subjects.add(new Subject(id, name, tId));
            persist();
        }
    }

    private void removeSelectedSubject(Subject subject) {
        if (subject == null) return;
        String tId = this.teacher != null ? this.teacher.getIdNumber() : null;
        
        // Use new teacher-scoped storage
        if (tId != null) {
            try {
                FileManager.TeacherSubjectFileManager tsfm = new FileManager.TeacherSubjectFileManager(tId);
                tsfm.deleteSubject(subject.getId());
                subjects.remove(subject);
                
                // Update view with fresh data from teacher file
                try {
                    java.util.List<String[]> summaries = new java.util.ArrayList<>();
                    for (Subject s : subjects) {
                        try {
                            FileManager.TeacherSubjectFileManager tempTsfm = new FileManager.TeacherSubjectFileManager(tId);
                            FileManager.TeacherSubjectFileManager.SubjectData data = tempTsfm.getSubject(s.getId());
                            int count = data != null ? data.getStudentCount() : 0;
                            summaries.add(new String[]{s.getId(), s.getName(), String.valueOf(count)});
                        } catch (Exception ex) {
                            summaries.add(new String[]{s.getId(), s.getName(), "0"});
                        }
                    }
                    view.setSubjectSummaries(summaries);
                } catch (Exception e) {
                    view.setSubjects(subjects);
                }
            } catch (Exception e) {
                view.showError("Failed to remove subject: " + e.getMessage());
            }
        } else {
            // Legacy fallback
            subjects.remove(subject);
            persist();
        }
    }

    private void persist() {
        try {
            // Merge: keep other teachers' subjects, replace this teacher's set
            java.util.List<Subject> all = fileManager.readAll();
            String tId = this.teacher != null ? this.teacher.getIdNumber() : null;
            java.util.List<Subject> merged = new java.util.ArrayList<>();
            for (Subject s : all) {
                if (tId != null && tId.equals(s.getTeacherId())) continue; // drop old ones for this teacher
                merged.add(s);
            }
            merged.addAll(this.subjects);
            fileManager.writeAll(merged);
            view.setSubjects(this.subjects);
        } catch (Exception e) {
            view.showError("Failed to save subjects: " + e.getMessage());
        }
    }

    public SubjectController(SubjectFileManager fileManager, TeacherSubjectDashboardView view, Model.Teacher teacher) {
        // Do not pre-load unfiltered subjects; load fresh filtered list for this teacher
        this.fileManager = fileManager;
        this.view = view;
        this.teacher = teacher;

        // Wire actions
        this.view.onAddSubject(this::addSubject);
        this.view.onRemoveSubject(this::removeSelectedSubject);
        this.view.onOpenSubject(this::openSubjectDashboard);

        // Load from new teacher-scoped storage
        try {
            String tId = teacher != null ? teacher.getIdNumber() : null;
            if (tId != null) {
                FileManager.TeacherSubjectFileManager tsfm = new FileManager.TeacherSubjectFileManager(tId);
                java.util.Map<String, FileManager.TeacherSubjectFileManager.SubjectData> subjectMap = tsfm.loadAllSubjects();
                
                this.subjects = new java.util.ArrayList<>();
                java.util.List<String[]> summaries = new java.util.ArrayList<>();
                
                for (FileManager.TeacherSubjectFileManager.SubjectData data : subjectMap.values()) {
                    this.subjects.add(new Subject(data.subjectId, data.subjectName, tId));
                    summaries.add(new String[]{data.subjectId, data.subjectName, String.valueOf(data.getStudentCount())});
                }
                
                this.view.setSubjectSummaries(summaries);
            } else {
                // Fallback for no teacher
                this.subjects = new java.util.ArrayList<>();
                this.view.setSubjects(this.subjects);
            }
        } catch (FileReadException e) {
            this.view.showError("Failed to load subjects: " + e.getMessage());
            this.subjects = new java.util.ArrayList<>();
            this.view.setSubjects(this.subjects);
        }

        // Set greeting in header
        this.view.setTeacherGreeting(teacher.getName());

        // Logout wiring
        this.view.addLogoutListener(e -> {
            View.LoginView loginView = new View.LoginView();
            new Controller.LoginController(loginView, Service.AuthService.getInstance());
            Utility.PageNavigation.switchViews(this.view, loginView);
        });
    }

    private void openSubjectDashboard(Subject subject) {
        if (subject == null) return;
        View.TeacherDashboardView dashView = new View.TeacherDashboardView();
        Model.Teacher effectiveTeacher = this.teacher != null ? this.teacher : new Model.Teacher(subject.getTeacherId(), "", "");
        new Controller.TeacherDashboardController(dashView, effectiveTeacher, subject);
        dashView.setTitle("Flippio - " + subject.getName() + " Quizzes");
        Utility.PageNavigation.switchViews(this.view, dashView);
    }
}
