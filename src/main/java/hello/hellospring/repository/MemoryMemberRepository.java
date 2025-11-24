package hello.hellospring.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import hello.hellospring.domain.Member;

@Repository
public class MemoryMemberRepository implements MemberRepository {

    private static long sequence = 0L;
    private static final Map<Long, Member> store = new HashMap<>();

    // 파일 저장 위치 (프로젝트 루트의 data 폴더)
    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DATA_FILE = DATA_DIR.resolve("members.txt");

    public MemoryMemberRepository() {
        // 시작 시 파일에서 로드
        try {
            if (Files.notExists(DATA_DIR)) {
                Files.createDirectories(DATA_DIR);
            }
            if (Files.exists(DATA_FILE)) {
                List<String> lines = Files.readAllLines(DATA_FILE, StandardCharsets.UTF_8);
                for (String line : lines) {
                    // format: id|name|loginId|password|phoneNumber
                    if (line == null || line.isBlank()) continue;
                    String[] parts = line.split("\\|", -1);
                    if (parts.length >= 5) {
                        try {
                            Long id = Long.parseLong(parts[0]);
                            Member m = new Member();
                            m.setId(id);
                            m.setName(parts[1]);
                            m.setLoginId(parts[2]);
                            m.setPassword(parts[3]);
                            m.setPhoneNumber(parts[4]);
                            store.put(id, m);
                            if (id > sequence) sequence = id;
                        } catch (NumberFormatException e) {
                            // skip malformed id line
                        }
                    }
                }
            } else {
                // 파일 없으면 생성
                Files.createFile(DATA_FILE);
            }
        } catch (IOException e) {
            // 데이터 파일 로드 실패 시 콘솔에 표시(실무: 로깅)
            System.err.println("Failed to initialize data file: " + e.getMessage());
        }
    }

    @Override
    public Member Save(Member member) {
        member.setId(++sequence); // id 값 세팅
        store.put(member.getId(), member); // 저장소에 넣기
        // 변경된 전체를 파일에 덮어쓰기
        persistToFile();
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Member> findByName(String name) {
        return store.values().stream()
                .filter(member -> member.getName() != null && member.getName().equals(name))
                .findAny();
    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        // 로그인 ID로 저장소를 검색
        return store.values().stream()
                .filter(member -> loginId != null && loginId.equals(member.getLoginId()))
                .findAny();
    }

    @Override
    public List<Member> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void clearStore() {
        store.clear();
        sequence = 0L;
        persistToFile();
    }

    @Override
    public void update(Member member) {
        if (member == null || member.getId() == null) return;
        store.put(member.getId(), member);
        persistToFile();
    }

    private void persistToFile() {
        try {
            List<String> lines = store.values().stream()
                    .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                    .map(m -> String.format("%d|%s|%s|%s|%s",
                            m.getId(),
                            safe(m.getName()),
                            safe(m.getLoginId()),
                            safe(m.getPassword()),
                            safe(m.getPhoneNumber())))
                    .collect(Collectors.toList());
            Files.write(DATA_FILE, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("Failed to persist members to file: " + e.getMessage());
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.replace("|", " ");
    }
}
