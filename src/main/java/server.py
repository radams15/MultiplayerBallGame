import socket

HOST = "0.0.0.0"  # Standard loopback interface address (localhost)
PORT = 12349        # Port to listen on (non-privileged ports are > 1023)

BUFFER = 1024

class Game():
	players = []

	def run(self):
		while True:
			for player in self.players:
				data = player.recv(BUFFER)
				if data:
					if player == self.players[0]:
						print("1 ==> {}".format(data.decode()))
						self.players[1].send(data)
					else:
						print("2 ==> {}".format(data.decode()))
						self.players[0].send(data)
						
				if data.decode().strip() == "FIN":
					print("Done!")
					return
		

game = Game()

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
	s.bind((HOST, PORT))
	
	s.listen()
	
	
	for i in range(2):
		conn, addr = s.accept()
		print("{} Connected".format(addr))
		game.players.append(conn)
		
	game.run()
