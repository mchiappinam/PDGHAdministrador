package me.mchiappinam.pdghadministrador;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static String mysql_url = "jdbc:mysql://localhost:3306/administrador";
    public static String mysql_user = "root";
    public static String mysql_pass = "5ebFj1EYOhpyu0tJ47";
    public static String servidor = "ERRO";
	List<String> cmd = new ArrayList<String>();
    public boolean desligando=false;
	
	public void onEnable() {
		File file = new File(getDataFolder(),"config.yml");
		if(!file.exists()) {
			try {
				saveResource("config_template.yml",false);
				File file2 = new File(getDataFolder(),"config_template.yml");
				file2.renameTo(new File(getDataFolder(),"config.yml"));
			}catch(Exception e) {}
		}
		try {
			Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
			if (con == null) {
				getLogger().warning("ERRO: Conexao ao banco de dados MySQL falhou!");
				getServer().getPluginManager().disablePlugin(this);
			}else{
				Statement st = con.createStatement();
				st.execute("CREATE TABLE IF NOT EXISTS `log` ( `id` MEDIUMINT NOT NULL AUTO_INCREMENT, `servidor` text, `staffer` text, `log` text, `data` text, PRIMARY KEY (`id`))");
				st.execute("CREATE TABLE IF NOT EXISTS `servidores` ( `servidor` text)");
				st.execute("CREATE TABLE IF NOT EXISTS `sendcmd` ( `servidor` text, `staffer` text, `cmd` text)");
				//st.execute("ALTER TABLE  `rankpvp` CHANGE  `nome` `nome` VARCHAR(30)"); /* Retira código na próxima versão */
				st.close();
				getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3Conectado ao banco de dados MySQL!");
			}
			con.close();
		}catch (SQLException e) {
			getLogger().warning("ERRO: Conexao ao banco de dados MySQL falhou!");
			getLogger().warning("ERRO: "+e.toString());
			getServer().getPluginManager().disablePlugin(this);
		}
		
		servidor=getConfig().getString("servidor");
		getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §2Servidor detectado: "+servidor);
		desligando=false;
		updateDBServidor();
		getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §2ativando timer...");
		check();
		checkCMD();
		getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §2ativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §2Acesse: http://pdgh.com.br/");
	}

	public void onDisable() {
		desligando=true;
		updateDBServidor();
		getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §2desativado - Plugin by: mchiappinam");
		getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §2Acesse: http://pdgh.com.br/");
	}
	
	public void check() {
	  	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			all();
	  		}
	  	}, 0, 2*20);
	}
	
	public void checkCMD() {
	  	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	  		public void run() {
	  			comando();
	  		}
	  	}, 0, 15);
	}
	
	public void comando() {
		if(cmd.size()!=0)
			for(String comando : cmd) {
				getServer().dispatchCommand(getServer().getConsoleSender(), comando);
				cmd.remove(comando);
			}
	}

	public void updateDBServidor() {
		try {
        	Connection con = DriverManager.getConnection(mysql_url,mysql_user,mysql_pass);
			//Prepared statement
			PreparedStatement pst = con.prepareStatement("SELECT * FROM `servidores` WHERE( `servidor`='"+servidor.trim()+"');");
			ResultSet rs = pst.executeQuery();
			boolean existe=false;
            while(rs.next()) {
            	if(desligando) {
					getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3O servidor está sendo fechado...");
					getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3Removendo o servidor "+servidor+" do banco de dados de servidores ativos...");
	            	PreparedStatement pst2 = con.prepareStatement("DELETE FROM `servidores` WHERE( `servidor`='"+servidor.trim()+"');");
					pst2.executeUpdate();
					pst2.close();
					getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3Servidor "+servidor+" removido do banco de dados.");
            	}
            	existe=true;
            }
            if((!desligando)&&(!existe)) {
            	getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3O servidor está sendo iniciado e não foi detectado o banco de dados de servidores ativos...");
				getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3Inserindo o servidor "+servidor+" do banco de dados de servidores ativos...");
            	PreparedStatement pst2 = con.prepareStatement("INSERT INTO servidores(servidor) VALUES(?)");
				pst2.setString(1, servidor.trim());
				pst2.executeUpdate();
				pst2.close();
				getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3Servidor "+servidor+" inserido no banco de dados.");
            }else{
				getServer().getConsoleSender().sendMessage("§3[PDGHAdministrador] §3Servidor "+servidor+" já existente no banco de dados de servidores ativos.");
            }
			rs.close();
			pst.close();
			con.close();
		}catch (SQLException ex) {
			System.out.print(ex);
			getServer().getConsoleSender().sendMessage("§cErro! Contate um staffer!");
        }
	}
	
	public void all() {
		Threads t = new Threads(this, "all");
		t.start();
	}
  
}