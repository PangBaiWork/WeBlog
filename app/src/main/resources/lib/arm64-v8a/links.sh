
lines=15
tail -n+$lines $0 | while IFS= read -r line; do
    dir_path=$(dirname "$line")
    if [ ! -d "$dir_path" ]; then
       mkdir -p "$dir_path"
    fi
    filename=$(basename "$line")
    ln -s -f "$1/$filename" "$line"
done 

return 0


/data/data/com.pangbai.weblog/files/usr/bin/hugo
/data/data/com.pangbai.weblog/files/usr/bin/derb
/data/data/com.pangbai.weblog/files/usr/bin/icuinfo
/data/data/com.pangbai.weblog/files/usr/bin/gencfu
/data/data/com.pangbai.weblog/files/usr/bin/gencnval
/data/data/com.pangbai.weblog/files/usr/bin/genrb
/data/data/com.pangbai.weblog/files/usr/bin/gennorm2
/data/data/com.pangbai.weblog/files/usr/bin/gencmn
/data/data/com.pangbai.weblog/files/usr/bin/pkgdata
/data/data/com.pangbai.weblog/files/usr/bin/uconv
/data/data/com.pangbai.weblog/files/usr/bin/escapesrc
/data/data/com.pangbai.weblog/files/usr/bin/genccode
/data/data/com.pangbai.weblog/files/usr/bin/gendict
/data/data/com.pangbai.weblog/files/usr/bin/genbrk
/data/data/com.pangbai.weblog/files/usr/bin/icuexportdata
/data/data/com.pangbai.weblog/files/usr/bin/makeconv
/data/data/com.pangbai.weblog/files/usr/bin/icupkg
/data/data/com.pangbai.weblog/files/usr/bin/gensprep
/data/data/com.pangbai.weblog/files/usr/bin/node
/data/data/com.pangbai.weblog/files/usr/bin/openssl
/data/data/com.pangbai.weblog/files/usr/bin/scalar
/data/data/com.pangbai.weblog/files/usr/bin/less
/data/data/com.pangbai.weblog/files/usr/bin/lessecho
/data/data/com.pangbai.weblog/files/usr/bin/lesskey
/data/data/com.pangbai.weblog/files/usr/bin/tset
/data/data/com.pangbai.weblog/files/usr/bin/clear
/data/data/com.pangbai.weblog/files/usr/bin/db_hotbackup
/data/data/com.pangbai.weblog/files/usr/bin/db_load
/data/data/com.pangbai.weblog/files/usr/bin/db_stat
/data/data/com.pangbai.weblog/files/usr/bin/db_log_verify
/data/data/com.pangbai.weblog/files/usr/bin/db_replicate
/data/data/com.pangbai.weblog/files/usr/bin/db_upgrade
/data/data/com.pangbai.weblog/files/usr/bin/db_convert
/data/data/com.pangbai.weblog/files/usr/bin/db_recover
/data/data/com.pangbai.weblog/files/usr/bin/db_tuner
/data/data/com.pangbai.weblog/files/usr/bin/db_checkpoint
/data/data/com.pangbai.weblog/files/usr/bin/db_printlog
/data/data/com.pangbai.weblog/files/usr/bin/db_dump
/data/data/com.pangbai.weblog/files/usr/bin/db_verify
/data/data/com.pangbai.weblog/files/usr/bin/db_deadlock
/data/data/com.pangbai.weblog/files/usr/bin/db_archive
/data/data/com.pangbai.weblog/files/usr/bin/kdestroy
/data/data/com.pangbai.weblog/files/usr/bin/kswitch
/data/data/com.pangbai.weblog/files/usr/bin/kdb5_util
/data/data/com.pangbai.weblog/files/usr/bin/gss-server
/data/data/com.pangbai.weblog/files/usr/bin/klist
/data/data/com.pangbai.weblog/files/usr/bin/kpropd
/data/data/com.pangbai.weblog/files/usr/bin/krb5kdc
/data/data/com.pangbai.weblog/files/usr/bin/sim_server
/data/data/com.pangbai.weblog/files/usr/bin/kvno
/data/data/com.pangbai.weblog/files/usr/bin/sim_client
/data/data/com.pangbai.weblog/files/usr/bin/gss-client
/data/data/com.pangbai.weblog/files/usr/bin/ksu
/data/data/com.pangbai.weblog/files/usr/bin/ktutil
/data/data/com.pangbai.weblog/files/usr/bin/uuserver
/data/data/com.pangbai.weblog/files/usr/bin/kadmind
/data/data/com.pangbai.weblog/files/usr/bin/kadmin
/data/data/com.pangbai.weblog/files/usr/bin/uuclient
/data/data/com.pangbai.weblog/files/usr/bin/kprop
/data/data/com.pangbai.weblog/files/usr/bin/kpasswd
/data/data/com.pangbai.weblog/files/usr/bin/kadmin.local
/data/data/com.pangbai.weblog/files/usr/bin/kinit
/data/data/com.pangbai.weblog/files/usr/bin/sserver
/data/data/com.pangbai.weblog/files/usr/bin/kproplog
/data/data/com.pangbai.weblog/files/usr/bin/sclient
/data/data/com.pangbai.weblog/files/usr/bin/drill
/data/data/com.pangbai.weblog/files/usr/bin/infocmp
/data/data/com.pangbai.weblog/files/usr/bin/tic
/data/data/com.pangbai.weblog/files/usr/bin/tabs
/data/data/com.pangbai.weblog/files/usr/bin/tput
/data/data/com.pangbai.weblog/files/usr/bin/toe
/data/data/com.pangbai.weblog/files/usr/bin/sftp
/data/data/com.pangbai.weblog/files/usr/bin/ssh-keyscan
/data/data/com.pangbai.weblog/files/usr/bin/sshd
/data/data/com.pangbai.weblog/files/usr/bin/ssh-keygen
/data/data/com.pangbai.weblog/files/usr/bin/ssh-agent
/data/data/com.pangbai.weblog/files/usr/bin/ssh
/data/data/com.pangbai.weblog/files/usr/bin/ssh-add
/data/data/com.pangbai.weblog/files/usr/bin/scp
/data/data/com.pangbai.weblog/files/usr/bin/passwd
/data/data/com.pangbai.weblog/files/usr/bin/pwlogin
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git-remote-http
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/scalar
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git-daemon
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git-imap-send
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git-http-backend
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git-http-fetch
/data/data/com.pangbai.weblog/files/usr/libexec/git-core/git-sh-i18n--envsubst
/data/data/com.pangbai.weblog/files/usr/libexec/sftp-server
/data/data/com.pangbai.weblog/files/usr/libexec/ssh-sk-helper
/data/data/com.pangbai.weblog/files/usr/libexec/ssh-keysign
/data/data/com.pangbai.weblog/files/usr/libexec/ssh-pkcs11-helper